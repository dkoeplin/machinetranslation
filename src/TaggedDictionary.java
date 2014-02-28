import java.io.BufferedReader;
import java.io.FileInputStream;
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
            String allTranslations = input.readLine();
            String[] curTrans = allTranslations.split(",");
            List<TaggedWord> E = new ArrayList<TaggedWord>();
            for (String e : curTrans) {
               String[] tags = e.split("/");
               String trans = tags[0].trim();
               String pos = (tags.length > 1) ? tags[1].trim() : "";
               String tense = (tags.length > 2) ? tags[2].trim() : "";
               E.add(new TaggedWord(trans, pos, tense));
               for (int i = 3; i < tags.length; i++)  // add other conjugations/forms of this word
                  E.add(new TaggedWord(trans, pos, tags[i].trim()));
            }
            translations.put(f.toLowerCase(), E);
            if (verbose) { System.out.print(f + "=> "); for (TaggedWord trans : E) { System.out.print(trans.word + "/" + trans.POS + ", "); } System.out.print("\n");} 
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
      return getWordTranslations(f, true);
   }
   public List<TaggedWord> getWordTranslations(TaggedWord f, boolean matchPOS) {
      if (matchPOS) {
         List<TaggedWord> E = new ArrayList<TaggedWord>();
         if (f.isAWord() && translations.containsKey(f.word.toLowerCase())) {
            List<TaggedWord> list = translations.get(f.word.toLowerCase());
            for (TaggedWord e : list) {
               if (f.matches(e))
                  E.add(new TaggedWord(restoreCapitalization(f.word, e.word), f.POS, f.tense));
            }
            if (E.isEmpty()) {
               System.out.println("No translation available for " + f.word + "/" + f.POS + "/" + f.tense + " - reverting to random word");
               E.add(getRandomTranslation(f));
            }
         }
         else 
            E.add(f);
         return E;
      }
      else 
         return translations.get(f.word.toLowerCase());
   }
   
   public TaggedWord getRandomTranslation(TaggedWord f) {
      List<TaggedWord> E = getWordTranslations(f, false);
      if (E == null || E.isEmpty())
         return f;
      else if (E.size() == 1)
         return E.get(0);
      else
         return E.get(rand.nextInt(E.size()));
   }
   
}
