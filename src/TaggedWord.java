
public class TaggedWord {
   public static final String punct = "PCT";
   public static final String space = "SPC";
   public static final String unk   = "UNK";
   
   final String word;
   final String POS;
   
   public TaggedWord(String w, String p) {
      word = w; 
      POS = p;
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