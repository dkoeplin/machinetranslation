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
         List<TaggedWord> possibleTranslations = dictionary.getWordTranslations(f);
         if (possibleTranslations.isEmpty()) {
            System.out.println("No translations found for " + f.word);
            System.exit(-1);
         }
         TaggedWord trans = targetModel.chooseBestGreedy(prevWord, possibleTranslations);
         translation.addWord(trans);
         if (trans.isAWord())
            prevWord = trans;
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
		   while (m.find()) {
			   //System.out.println("old figures: " + w);
			   w = w.replace(".", ",");
			   //w.replace(",", "."); //
			   TaggedWord newWord = new TaggedWord(w, taggedWord.POS);
			   sentence.set(i, newWord);
			   //System.out.println("new figures: " + w);
		   }
		  
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
	
	public void switchNounAndAdjective(TaggedSentence t) {
		List<TaggedWord> sentence = t.getSentence();
		for (int i = 0; i < sentence.size(); i++) {
			TaggedWord current = sentence.get(i);
			if (i > 0 && current.POS.equals("ADJ")) {
				//System.out.println("adj: " + current.word);
				TaggedWord previous = sentence.get(i - 2);
				//System.out.println("noun: " + previous.word);
				if (previous.POS.equals("NC")) {
					System.out.println("noun: " + previous.word);
					TaggedWord newCurrent = new TaggedWord(previous.word, previous.POS);
					TaggedWord newPrevious = new TaggedWord(current.word, current.POS);
					
					sentence.set(i, newCurrent);
					sentence.set(i-2, newPrevious);
				}
			}
		}
	}
   
   public static void main(String[] args) {  
      TaggedDictionary dictionary = new TaggedDictionary("dictionary.txt");
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
          translator.noBeforeVerb(sentence);
          translator.randomTranslation(sentence).print();
          translator.processFigures(sentence);
          translator.switchNounAndAdjective(sentence);
          translator.randomTranslation(sentence).print();
          translator.modelTranslation(sentence).print();
          System.out.println("");
      }
   }
}
