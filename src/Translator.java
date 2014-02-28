import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Translator {
   TaggedDictionary dictionary;
   LanguageModel targetModel;
   Pattern wordPattern = Pattern.compile("[\\p{L}\\w].*[\\p{L}\\w]|[\\w\\p{L}]");
   
   public Translator(TaggedDictionary dict, LanguageModel model) {
      dictionary = dict;
      targetModel = model;
   }
   public TaggedSentence multiBigramModelTranslation(TaggedSentence sentence) {
      return multiBigramModelTranslation(sentence, true, true);
   }
   public TaggedSentence multiBigramModelTranslation(TaggedSentence sentence, boolean usePOS, boolean useTense) {
      TaggedSentence translation = new TaggedSentence();
      
      sentence.initIter();
      TaggedWord prevWord = new TaggedWord("", "");
      List<TaggedWord> curChoices = null;
      List<TaggedWord> intermediates = new ArrayList<TaggedWord>(); 
      int index = 0;
      while (sentence.hasNext()) {
         TaggedWord f = sentence.next();
         if (f.isAWord()) {
            List<TaggedWord> possibleNexts = dictionary.getWordTranslations(f, usePOS, useTense);
            TaggedWord trans = null;
            if (index > 0) {
               trans = targetModel.chooseBestTri(prevWord, curChoices, possibleNexts);
               translation.addWord(trans);
               for (TaggedWord sp : intermediates) 
                  translation.addWord(sp);
               intermediates.clear();
            }
            else
               trans = new TaggedWord("", "");
            curChoices = possibleNexts;
            prevWord = trans; 
            index++;
         }
         else
            intermediates.add(f);
      }
      // Translate last word in sequence
      translation.addWord(targetModel.chooseBestTri(prevWord, curChoices, null));
      for (TaggedWord sp : intermediates) 
         translation.addWord(sp);
      
      return translation;
   }
   
   public TaggedSentence bigramModelTranslation(TaggedSentence sentence) {
      TaggedSentence translation = new TaggedSentence();
      
      sentence.initIter();
      TaggedWord prevWord = new TaggedWord("","");
      while (sentence.hasNext()) {
         TaggedWord f = sentence.next();
         if (f.isAWord()) {
            List<TaggedWord> possibleTranslations = dictionary.getWordTranslations(f);
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
   	   
   public static void main(String[] args) {  
      TaggedDictionary dictionary = new TaggedDictionary("dictionary.txt", false);
      LanguageModel model = new LanguageModel("bigrams.txt");
      Translator translator = new Translator(dictionary, model);
      TreeTagger spanishPOS = new TreeTagger(System.getProperty("user.dir") + "/TreeTagger",
                                                "cmd/tree-tagger-spanish-utf8");
      
      // Load sentences from file
      List<TaggedSentence> spanish_dev = loadAndTagSentences("sentences_dev.txt", spanishPOS);
      List<TaggedSentence> spanish_test = loadAndTagSentences("sentences_test.txt", spanishPOS);
      
      // Translate sentences and output to terminal
      ReplaceWithAn replaceWithAn = new ReplaceWithAn();
      ProcessFigures processFigures = new ProcessFigures();
      ProcessNegation processNegation = new ProcessNegation();
      RearrangedModifiers rearrangedModifiers = new RearrangedModifiers();
      CheckAmounts checkAmounts = new CheckAmounts();
      MissingSubjects missingSubjects = new MissingSubjects();
      CheckArticles checkArticles = new CheckArticles();
      
      // Baseline translations (parts of speech not actually used)
      System.out.println("Baseline Translations");
      System.out.println("Development Set");
      for (TaggedSentence sentence : spanish_dev)
         translator.randomTranslation(sentence).print();
      System.out.println("");
      System.out.println("Training Set");
      for (TaggedSentence sentence : spanish_test) 
         translator.randomTranslation(sentence).print();
      System.out.println("");
      System.out.println("");
      
      for (TaggedSentence sentence : spanish_dev) {
         sentence.print(true);
    	 processNegation.applyStrategy(sentence);
    	 checkArticles.applyStrategy(sentence);
         rearrangedModifiers.applyStrategy(sentence);
        
         TaggedSentence trans = translator.multiBigramModelTranslation(sentence);
         replaceWithAn.applyStrategy(trans);
         processFigures.applyStrategy(trans);
         checkAmounts.applyStrategy(trans);
         missingSubjects.applyStrategy(trans);
         trans.print();
         System.out.println("");
      }
      
      for (TaggedSentence sentence : spanish_test) {
         sentence.print();
         processNegation.applyStrategy(sentence);
         checkArticles.applyStrategy(sentence);
         rearrangedModifiers.applyStrategy(sentence);
         
         TaggedSentence trans = translator.multiBigramModelTranslation(sentence);
         replaceWithAn.applyStrategy(trans);
         processFigures.applyStrategy(trans);
         checkAmounts.applyStrategy(trans);
         missingSubjects.applyStrategy(trans);
         trans.print();
         System.out.println("");
      }
   }
}
