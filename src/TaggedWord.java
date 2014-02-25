import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaggedWord {
   private static final String punct = "PCT";
   private static final String space = "SPC";
   private static final String unk   = "UNK";
   
   private static Pattern wordPattern = Pattern.compile("[\\p{L}\\w].*[\\p{L}\\w]|[\\w\\p{L}]");
   private static Pattern spcPattern = Pattern.compile("\\s+");
   
   final String word;
   final String POS;
   
   public TaggedWord(String w, String p) {
      word = w; 
      Matcher wfind = wordPattern.matcher(w);
      Matcher sfind = spcPattern.matcher(w);
      POS = (wfind.find()) ? p : 
            (sfind.find()) ? space : 
            (w.equals("")) ? unk : punct;
   }
   
   public boolean samePOS(TaggedWord other) {
      return this.POS.equals(other.POS);
   }
   
   public boolean POSisUnk() {
      return this.POS.equals("") || this.POS.equals(unk);
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