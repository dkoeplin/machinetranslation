import java.util.List;


public class CheckAmounts {

	public CheckAmounts() {}
	
	public void applyStrategy(TaggedSentence t) {
		List<TaggedWord> sentence = t.getSentence();
		 for (int i = 0; i < sentence.size(); i++) {
			   TaggedWord w = sentence.get(i);
			   if ((w.word.equals("millones") || w.word.equals("miles")) && i < sentence.size() - 2) {
				   if (sentence.get(i+2).word.equals("de")) {
					   String english;
					   if (w.word.equals("millones")) english = "million";
					   else english = "thousand";
					   sentence.set(i, new TaggedWord(english, w.POS));
					   sentence.remove(i+1);
					   sentence.remove(i+1);
				   }
			   }
		 }
	}
}
