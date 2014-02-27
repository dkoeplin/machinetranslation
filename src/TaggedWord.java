import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaggedWord {
   private static final String punct = "PCT";
   private static final String space = "SPC";
   private static final String wild   = "ANY";
   
   private static Pattern wordPattern = Pattern.compile("[\\p{L}\\w].*[\\p{L}\\w]|[\\w\\p{L}]");
   private static Pattern spcPattern = Pattern.compile("\\s+");
   
   final String word;
   final String POS;
   
   public TaggedWord(String w, String p) {
      word = w; 
      Matcher wfind = wordPattern.matcher(w);
      Matcher sfind = spcPattern.matcher(w);

      if (wfind.find()) 
         POS = (p.equals("")) ? wild : p;
      else
         POS = (sfind.find()) ? space : punct;
   }
   
   public boolean isNoun() { return this.POS.startsWith("N"); }
   public boolean isAdj() { return this.POS.startsWith("ADJ"); }
   public boolean isVerb() { return this.POS.startsWith("V"); }
   public boolean isAdv() { return this.POS.startsWith("ADV"); }
   
   /* Does the given have the same POS as some other word?
      POS are made to be somewhat general in the dictionary in some cases 
      so they are considered to be matching if the tag from the tagger contains
      the tag from the dictionary */
   public boolean samePOS(TaggedWord other) {
      if (other.POSisUnk()) 
         return true;
      return this.POS.contains(other.POS);
   }
   
   public boolean POSisUnk() {
      return this.POS.equals("") || this.POS.equals(wild);
   }
   
   public boolean isAWord() {
      return !this.POS.equals(punct) && !this.POS.equals(space);
   }
   // SPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACE!
   public boolean isSpace() {
      return this.POS.equals(space);
   }
   public boolean isPunct() {
      return this.POS.equals(punct);
   }
}