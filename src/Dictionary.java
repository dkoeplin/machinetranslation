import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Dictionary {
   Map<String, List<String>> translations;
   static Random rand = new Random();
   
   // Create and populate dictionary from given dictionary file
   // Dictionary entries are formatted as two lines. The first line is the word (or phrase)
   // to be translated, the second is a comma separated list of possible translations. Ex:
   //       Word 1
   //       Translation 1, Translation 2, Translation 3
   //       Word 2
   //       Translation 4
   //       etc.
   public Dictionary(String filename) { this(filename, false); }
   public Dictionary(String filename, boolean verbose) {
      translations = new HashMap<String, List<String>>();
      try {
         BufferedReader input = new BufferedReader(new FileReader(filename));
         String f = input.readLine();
         while (f != null) {
            f = f.replaceAll("[^\\w]", "");  // replace non-word characters like apostrophes
            String allTranslations = input.readLine();
            String[] curTrans = allTranslations.split(",");
            List<String> E = new ArrayList<String>();
            for (String e : curTrans)
               E.add(e.trim());
            
            translations.put(f, E);
            if (verbose) { System.out.print(f + "=> "); for (String trans : E) { System.out.print(trans + ", "); } System.out.print("\n");} 
            f = input.readLine();
         }
         input.close();
      } 
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
   
   public String translateWord(String word) {
      if (translations.containsKey(word)) {
         List<String> list = translations.get(word);
         return list.get(rand.nextInt(list.size()));
      }
      return word;
   }
   
}
