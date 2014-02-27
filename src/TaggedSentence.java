import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaggedSentence {
   private List<TaggedWord> sentence;
   private Iterator<TaggedWord> iterator = null;
   
   public TaggedSentence() {
      sentence = new ArrayList<TaggedWord>();
   }
   
   public List<TaggedWord> getSentence() {
	   return sentence;
   }
   
   public void initIter() {
      iterator = sentence.iterator();
   }
   public boolean hasNext() {
      if (iterator == null) {return false;}
      return iterator.hasNext();
   }
   public TaggedWord next() {
      if (iterator == null) {return null;}
      return iterator.next();
   }
   
    
   public void addWord(String word, String POS) {
      if (word != null && !word.equals(""))
         sentence.add(new TaggedWord(word, POS));
   }
   public void addWord(TaggedWord word) {
      if (word != null)
         sentence.add(word);
   }
   
   public void print() { print(false, false);}
   public void print(boolean withTags) { print(withTags, false);}
   public void print(boolean withTags, boolean breaks) {
      for (TaggedWord w : sentence) {
         System.out.print(w.word);
         if (withTags && w.isAWord())
            System.out.print("/"+w.POS);
         if (breaks) {
            if (w.isSpace()) System.out.print("[SPACE]");
            System.out.print("\n");
         }
      }
      System.out.print("\n");
   }
}
