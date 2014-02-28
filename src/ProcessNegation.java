import java.util.List;


public class ProcessNegation {
	
	public ProcessNegation() {}
	
	public void applyStrategy(TaggedSentence t) {
		   List<TaggedWord> sentence = t.getSentence();
		   for (int i = 0; i < sentence.size(); i++) {
			   TaggedWord w = sentence.get(i);
			   
			   if (w.word.equals("no") && i < sentence.size()-2) {
				   TaggedWord nextWord = sentence.get(i+2);
				   if (nextWord.POS.equals("VSfin") || nextWord.POS.equals("VEfin")) {
					   TaggedWord current = new TaggedWord(nextWord.word, nextWord.POS);
					   TaggedWord next = new TaggedWord("not", w.POS);
					   sentence.set(i, current);
					   sentence.set(i+ 2, next);
				   }
			   }
		   }
		}
}
