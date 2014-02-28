import java.util.List;


public class ReplaceWithAn {

	public ReplaceWithAn() { }
	
	void applyStrategy(TaggedSentence t) {
		List<TaggedWord> sentence = t.getSentence();
		for (int i = 0; i < sentence.size(); i++) {
			TaggedWord w = sentence.get(i);
			if (w.word.equals("a") && i < sentence.size()-2) {
				if ("aeiou".contains(sentence.get(i+2).word.substring(0, 1)))
					sentence.set(i, new TaggedWord("an", w.POS));
			}
		 }
	}
}
