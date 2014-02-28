import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaggedWord {
   private static final String punct = "PCT";
   private static final String space = "SPC";
   private static final String wild  = "ANY";
  
   // Verb tenses
   private static final String past  = "PAST";
   private static final String we    = "WE";    // first person plural
   private static final String you   = "YOU";   // second person (sing or plural)
   private static final String they  = "THEY";  // third person plural
   private static final String me    = "ME";    // first person singular
   private static final String she   = "SHE";   // third person singular
   
   // Noun "tenses"
   private static final String sing  = "S";  // Singular noun
   private static final String plur  = "P";  // Plural noun
   
   private static Pattern wordPattern = Pattern.compile("[\\p{L}\\w].*[\\p{L}\\w]|[\\w\\p{L}]");
   private static Pattern spcPattern = Pattern.compile("\\s+");
   
   final String word;
   final String POS;
   final String tense;
   
   // if ends in os - always first person plural (we)
   // else if ends in is - always second person plural (you)
   // else if ends in n  - always third person plural (they)
   // else if: ends in s or ste - second person singular (you)
   // else if: ends in o or i' or e' - first person singular (I)
   // else if: ends in o' - third person singular (he/she/it)
   // otherwise guess third person singular
   
   // Nouns - ends in s means plural (probably)
   
   public TaggedWord(String w, String p, String t) {
      word = w; 
      Matcher wfind = wordPattern.matcher(w);
      Matcher sfind = spcPattern.matcher(w);

      if (p.equals("NEG")) {p = "neg";}
      
      if (wfind.find()) 
         POS = (p.equals("") || p.equals("PE")) ? wild : p;
      else
         POS = (sfind.find()) ? space : punct;
      
      tense = t;
      if (this.isNoun() && !(tense.equals(plur) || tense.equals(sing)) ) {
         System.out.println("Entry " + w + " has unknown tense " + t);
         System.exit(-1);
      }
      if (this.isVerb() && !(tense.equals(past) || tense.equals(we) || tense.equals(she) || tense.equals(you) 
            || tense.equals(they) || tense.equals(me) || tense.equals("ger") || tense.equals("inf"))) {
         System.out.println("Entry " + w + " has unknown tense " + t);
         System.exit(-1);
      }
   }
   public TaggedWord(String w, String p) {
      word = w; 
      Matcher wfind = wordPattern.matcher(w);
      Matcher sfind = spcPattern.matcher(w);

      if (p.equals("CARD")) {p = "NC";}
      if (p.equals("NEG")) {p = "neg";}
      
      if (wfind.find()) 
         POS = (p.equals("") || p.equals("PE")) ? wild : p;
      else
         POS = (sfind.find()) ? space : punct;
      
      if (POS.equals("VEadj") || POS.equals("VHadj") || POS.equals("VLadj") || POS.equals("VMadj") || POS.equals("VSadj")) {
            tense = past;
      }
      else if (POS.startsWith("V") && POS.endsWith("fin")) {
         if (w.endsWith("os")) 
            tense = we;
         else if (w.endsWith("is"))
            tense = you;
         else if (w.endsWith("n"))
            tense = they;
         else if (w.endsWith("s") || w.endsWith("ste"))
            tense = you;
         else if (w.endsWith("o") || w.endsWith("í") || w.endsWith("é"))
            tense = me;
         else
            tense = she;
      }
      else if (POS.startsWith("V")) 
         tense = POS.substring(POS.length() - 3, POS.length());
      else if (POS.startsWith("N"))
         if (w.endsWith("s"))
            tense = plur;
         else 
            tense = sing;
      else 
         tense = "";
   }
   
   public boolean isNoun() { return this.POS.startsWith("N"); }
   public boolean isAdj() { return this.POS.startsWith("ADJ"); }
   public boolean isVerb() { return this.POS.startsWith("V"); }
   public boolean isAdv() { return this.POS.startsWith("ADV"); }
   
   /* Does the given have the same POS as some other word?
      POS are made to be somewhat general in the dictionary in some cases 
      so they are considered to be matching if the tag from the tagger contains
      the tag from the dictionary */
   public boolean matches(TaggedWord other) {
      if (this.isNoun() || this.isVerb()) {
         return this.tense.equals(other.tense) && (other.POSisUnk() || this.POS.contains(other.POS));
      }
      else
         return other.POSisUnk() || this.POS.contains(other.POS);
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