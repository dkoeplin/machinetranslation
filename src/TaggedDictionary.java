import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TaggedDictionary {
   Map<String, List<TaggedWord>> translations;
   static Random rand = new Random();
   
   // Create and populate dictionary from given dictionary file
   // Dictionary entries are formatted as two lines. The first line is the word (or phrase)
   // to be translated, the second is a comma separated list of possible translations. Ex:
   //       Word 1
   //       Translation 1, Translation 2, Translation 3
   //       Word 2
   //       Translation 4
   //       etc.
   public TaggedDictionary(String filename) { this(filename, false); }
   public TaggedDictionary(String filename, boolean verbose) {
      translations = new HashMap<String, List<TaggedWord>>();
      try {
         BufferedReader input = new BufferedReader( new InputStreamReader(new FileInputStream(filename), "UTF-8"));
         String f = input.readLine();
         while (f != null) {
            //f = f.replaceAll("[^\\w]", "");  // replace non-word characters like apostrophes
            String allTranslations = input.readLine();
            String[] curTrans = allTranslations.split(",");
            List<TaggedWord> E = new ArrayList<TaggedWord>();
            for (String e : curTrans) {
               String[] tags = e.split("/");
               String trans = tags[0].trim();
               String pos = (tags.length > 1) ? tags[1].trim() : "";
               E.add(new TaggedWord(trans, pos));
            }
            translations.put(f.toLowerCase(), E);
            if (verbose) { System.out.print(f + "=> "); for (TaggedWord trans : E) { System.out.print(trans.word + ", "); } System.out.print("\n");} 
            f = input.readLine();
         }
         input.close();
      } 
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
   
   // Capitalizes first word if necessary
   private String restoreCapitalization(String orig, String dest) {
      String output = dest;
      if (Character.isUpperCase(orig.charAt(0)))
         output = Character.toUpperCase(dest.charAt(0)) + dest.substring(1);
         
      return output;
   }
   
   public List<TaggedWord> getWordTranslations(TaggedWord f) {
      List<TaggedWord> E = new ArrayList<TaggedWord>();
      if (f.isAWord() && translations.containsKey(f.word.toLowerCase())) {
         List<TaggedWord> list = translations.get(f.word.toLowerCase());
         for (TaggedWord e : list) {
            if (e.samePOS(f))
               E.add(new TaggedWord(restoreCapitalization(f.word, e.word), e.POS));
            else if (e.POSisUnk()) 
               E.add(new TaggedWord(restoreCapitalization(f.word, e.word), f.POS));
         }
      }
      else 
         E.add(f);
      return E;
   }
   
   public TaggedWord getRandomTranslation(TaggedWord f) {
      List<TaggedWord> E = getWordTranslations(f);
      if (E.size() > 1)
         return E.get(rand.nextInt(E.size()));
      else if (E.size() == 1)
         return E.get(0);
      else
         return f;
   }
   
}
