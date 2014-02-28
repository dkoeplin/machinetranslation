import java.util.List;


public class CheckArticles {

	public CheckArticles () {}
	
	void applyStrategy(TaggedSentence s) {
		List<TaggedWord> sentence = s.getSentence();
		for (int i = 0; i < sentence.size(); i++) {
			TaggedWord current = sentence.get(i);
			if (current.isPlural() && i > 1) {
				TaggedWord previous = sentence.get(i - 2);
				if (previous.word.toLowerCase().equals("un") || previous.word.toLowerCase().equals("una") || 
					previous.word.toLowerCase().equals("el") || previous.word.toLowerCase().equals("la")) {
					sentence.set(i, new TaggedWord(current.word, "NC", "S"));
				}
			}
		}
	}
}
