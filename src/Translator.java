import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
   TaggedDictionary dictionary;
   LanguageModel targetModel;
   Pattern wordPattern = Pattern.compile("[\\p{L}\\w].*[\\p{L}\\w]|[\\w\\p{L}]");
   Pattern numberPattern = Pattern.compile("\\d+(\\.\\d{3})*(,\\d*)?");
   
   public Translator(TaggedDictionary dict, LanguageModel model) {
      dictionary = dict;
      targetModel = model;
   }
   
   public TaggedSentence modelTranslation(TaggedSentence sentence) {
      TaggedSentence translation = new TaggedSentence();
      
      sentence.initIter();
      TaggedWord prevWord = new TaggedWord("","");
      while (sentence.hasNext()) {
         TaggedWord f = sentence.next();
         if (f.isAWord()) {
            List<TaggedWord> possibleTranslations = dictionary.getWordTranslations(f);
            if (possibleTranslations.isEmpty()) {
               System.out.println("No translation available for " + f.word + "/" + f.POS);
            }
            //System.out.println("Translating " + prevWord.word + " [" + f.word + "]");
            TaggedWord trans = targetModel.chooseBestGreedy(prevWord, possibleTranslations);
            translation.addWord(trans);
            prevWord = trans;
         }
         else
            translation.addWord(f);
      }
      
      return translation;
   }
   
   // Performs a direct, word for word translation of a sentence based
   // on the translator's dictionary
   public TaggedSentence randomTranslation(TaggedSentence sentence) {
      TaggedSentence translation = new TaggedSentence();
              
      sentence.initIter();
      while (sentence.hasNext())
         translation.addWord(dictionary.getRandomTranslation(sentence.next()));
      
      return translation;
   }
   
   public static List<TaggedSentence> loadAndTagSentences(String filename, TreeTagger tagger) {
      List<TaggedSentence> sentences = new ArrayList<TaggedSentence>();
      try {
         BufferedReader input = new BufferedReader( new InputStreamReader(new FileInputStream(filename), "UTF8"));
         for(String line = input.readLine(); line != null; line = input.readLine())
            sentences.add(tagger.tagSentence(line.trim()));
         
         input.close();
      }
      catch(Exception e) {
         System.out.println("Failed while trying to tag sentences");
         e.printStackTrace();
         System.exit(1);
      }
      return sentences;
   }
   
	   /* public void missingSubject(TaggedSentence t) {
	   List<TaggedWord> sentence = t.getSentence();
	   for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord w = sentence.get(i);
	   }
	}*/
	
	/* Replaces periods in figures as commas. We can
	* attempt to change the commas to period (i.e 45,67 to 45.67)
	*/
	public void processFigures(TaggedSentence t) {
	   List<TaggedWord> sentence = t.getSentence();
	   Matcher m;
	   for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord taggedWord = sentence.get(i);
		   String w = taggedWord.word;
		   m = numberPattern.matcher(w);
		   while (m.find())
			   sentence.set(i, new TaggedWord(w.replace(".", ","), taggedWord.POS));
	   }
	   
	}
	public void noBeforeVerb(TaggedSentence t) {
	   List<TaggedWord> sentence = t.getSentence();
	   for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord w = sentence.get(i);
		   
		   if (w.word.equals("no") && i < sentence.size()-2) {
			   TaggedWord nextWord = sentence.get(i+2);
			   if (nextWord.POS.equals("VSfin") || nextWord.POS.equals("VEfin")) {
				   TaggedWord current = new TaggedWord(nextWord.word, nextWord.POS);
				   TaggedWord next = new TaggedWord("not", w.POS);
				   sentence.set(i, current);
				   sentence.set(i+ 2, next);
				 
				   //System.out.println("VSfin: " + nextWord.word);
			   }
		   }
	   }
	}
	
   /* Given a sequence of tagged words with possible sequence of nouns followed
    * by adjectives (e.g. N1 N2 N3 A1 A2 A3), flips sequence of adjectives and moves
    * it before the sequence of nouns
    */
	
	public void rearrangeModifiers(TaggedSentence s) {
		List<TaggedWord> sentence = s.getSentence();
		
		boolean targetIsNouns = false;
		boolean modifiers = false;
		
		List<TaggedWord> curTarg = new ArrayList<TaggedWord>();
		List<TaggedWord> curMod = new ArrayList<TaggedWord>();
		List<Integer> locs = new ArrayList<Integer>();
		
		for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord current = sentence.get(i);
		   boolean nounAdjStopper = current.isAWord() && targetIsNouns && !current.isAdj() && !current.isNoun();
		   boolean verbAdvStopper = current.isAWord() && !targetIsNouns && !current.isAdv() && !current.isVerb();
		   boolean endOfPhrase = current.isPunct() || i == sentence.size() - 1;
	      // If we've reached a word that is not a correct modifier or a target, a punction mark, or the end of the sentence
         if ((nounAdjStopper || verbAdvStopper || endOfPhrase) && (!curTarg.isEmpty() || !curMod.isEmpty()) ) {
            if (!curTarg.isEmpty() && !curMod.isEmpty()) {
               for (int pos = 0; pos < locs.size(); pos++) {
                  int loc = locs.get(pos);
                  if (pos >= curMod.size())
                     sentence.set(loc, curTarg.get(pos - curMod.size()));
                  else
                     sentence.set(loc, curMod.get(pos));
               }
            }
            curTarg.clear();
            curMod.clear();
            locs.clear();
            modifiers = false;
         }
		   
		   if (!modifiers && (current.isNoun() || current.isVerb()) ) {
		      curTarg.add(current);
		      locs.add(i);
		      targetIsNouns = current.isNoun();
		   }
		   // If we have at least one target word (noun or verb) and this matches the target type
		   else if (!curTarg.isEmpty() && ((current.isAdj() && targetIsNouns) || (current.isAdv() && !targetIsNouns))) {
		      curMod.add(0, current);
		      locs.add(i);
		      modifiers = true;
		   }
		}
	}
   
   public static void main(String[] args) {  
      TaggedDictionary dictionary = new TaggedDictionary("dictionary.txt", true);
      LanguageModel model = new LanguageModel("bigrams.txt");
      Translator translator = new Translator(dictionary, model);
      TreeTagger spanishPOS = new TreeTagger(System.getProperty("user.dir") + "/TreeTagger",
                                                "cmd/tree-tagger-spanish-utf8");
      
      // Load sentences from file
      List<TaggedSentence> spanish_dev = loadAndTagSentences("sentences_dev.txt", spanishPOS);
      List<TaggedSentence> spanish_test = loadAndTagSentences("sentences_test.txt", spanishPOS);
      
      // Translate sentences and output to terminal
      for (TaggedSentence sentence : spanish_dev) {
          sentence.print(); 
          translator.randomTranslation(sentence).print();
          sentence.print(true);
          translator.noBeforeVerb(sentence);
          //translator.randomTranslation(sentence).print();
          translator.processFigures(sentence);
          translator.modelTranslation(sentence).print();
          translator.rearrangeModifiers(sentence);
          //translator.randomTranslation(sentence).print();
          translator.modelTranslation(sentence).print();
          System.out.println("");
      }
   }
}
