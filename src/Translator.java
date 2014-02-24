import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
   Dictionary dictionary;
   Pattern wordPattern = Pattern.compile("\\w.*\\w");
   
   public Translator(Dictionary dict) {
      dictionary = dict;
   }
   
   // Performs a direct, word for word translation of a sentence based
   // on the translator's dictionary
   public List<String> directTranslation(List<String> sentences) {
      List<String> translations = new ArrayList<String>();
      if (sentences == null || sentences.isEmpty())
         return translations;
               
      // Iterate through sentences
      for (String sentence : sentences) {
         String translation = "";
         sentence.replaceAll("-", " ");      // replace dashes with spaces
         String [] words = sentence.split("\\s+");    // split on spaces
         
         // Iterate through words in sentence
         for (String sequence : words) {      
            Matcher m = wordPattern.matcher(sequence);
            if (m.find()) {
               // Find the word if one exists
               String word = m.group(0);
               // replace all non-word characters
               String f_word = word.replaceAll("[^\\w]", "").toLowerCase();
               String e_word = dictionary.translateWord(f_word);
               // replace the original word (before non-word char replacement) with the translated word
               translation += sequence.replace(word, e_word) + " ";
            }
            else
               translation += sequence + " ";
         }
         translations.add(translation.trim());
      }
      return translations;
   }
   
   public static void main(String[] args) {  
      Dictionary dictionary = new Dictionary("dictionary.txt", true);
      Translator translator = new Translator(dictionary);
      
      // Load sentences from file
      List<String> sentences = new ArrayList<String>();
      try {
         BufferedReader input = new BufferedReader(new FileReader("sentences.txt"));
         for(String line = input.readLine(); line != null; line = input.readLine())
            sentences.add(line.trim());
         
         input.close();
      }
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
    
      // Translate sentences and output to terminal
      List<String> translatedSentences = translator.directTranslation(sentences);
      for (String sentence : translatedSentences)
         System.out.println(sentence);
   }
}
