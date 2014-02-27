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
   Pattern wordPattern = Pattern.compile("[\\p{L}\\w].*[\\p{L}\\w]|[\\w\\p{L}]");
   
   public Translator(TaggedDictionary dict) {
      dictionary = dict;
   }
   
   // Performs a direct, word for word translation of a sentence based
   // on the translator's dictionary
   public List<TaggedSentence> directTranslation(List<TaggedSentence> sentences) {
      List<TaggedSentence> translations = new ArrayList<TaggedSentence>();
      if (sentences == null || sentences.isEmpty())
         return translations;
               
      // Iterate through sentences
      for (TaggedSentence sentence : sentences) {
         TaggedSentence translation = new TaggedSentence();
        
         sentence.initIter();
         while (sentence.hasNext()) {
            TaggedWord f = sentence.next();
            
            // List<TaggedWord> E = dictionary.getWordTranslations(f);
            translation.addWord(dictionary.getRandomTranslation(f));
         }
         translations.add(translation);
      }
      return translations;
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
      TaggedDictionary dictionary = new TaggedDictionary("dictionary.txt", true);
      Translator translator = new Translator(dictionary);
      TreeTagger spanishPOS = new TreeTagger(System.getProperty("user.dir") + "/TreeTagger",
                                                "cmd/tree-tagger-spanish-utf8");
      
      // Load sentences from file
      List<TaggedSentence> spanish_dev = loadAndTagSentences("sentences_dev.txt", spanishPOS);
      List<TaggedSentence> spanish_test = loadAndTagSentences("sentences_test.txt", spanishPOS);
      
      // Translate sentences and output to terminal
      List<TaggedSentence> translatedSentences = translator.directTranslation(spanish_dev);
      for (TaggedSentence sentence : translatedSentences)
         sentence.print(true);
   }
}
