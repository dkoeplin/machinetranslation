import java.util.List;


public class ProcessNegation {
	
	public ProcessNegation() {}
	
	public void applyStrategy(TaggedSentence t) {
      List<TaggedWord> sentence = t.getSentence();
      for (int i = 0; i < sentence.size(); i++) {
         TaggedWord w = sentence.get(i);
         if (w.POS.equals("neg") && i < sentence.size()-2) {
            TaggedWord nextWord = sentence.get(i+2);
            if (nextWord.POS.equals("VSfin") || nextWord.POS.equals("VEfin") || nextWord.POS.equals("VMfin")) {
               TaggedWord current = new TaggedWord(nextWord.word, nextWord.POS);
               TaggedWord next = new TaggedWord("not", w.POS);
               sentence.set(i, current);
               sentence.set(i+ 2, next);
            } else if (nextWord.POS.equals("VLfin")) {
            	if (nextWord.isPlural()) sentence.set(i, new TaggedWord("do not", "VDfin"));
            }
         }
      }
   }
}
