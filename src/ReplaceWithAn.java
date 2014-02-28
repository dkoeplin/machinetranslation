import java.util.List;


public class ReplaceWithAn {

	public ReplaceWithAn() { }
	
	void applyStrategy(TaggedSentence t) {
		List<TaggedWord> sentence = t.getSentence();
		for (int i = 0; i < sentence.size(); i++) {
			TaggedWord w = sentence.get(i);
			if (w.word.toLowerCase().equals("a") && i < sentence.size()-2) {
				if ("aeiou".contains(sentence.get(i+2).word.substring(0, 1))) {
					t.print(false);
					TaggedWord next = sentence.get(i + 2);
					System.out.println("next: " + next.word);
					sentence.set(i, new TaggedWord("an", w.POS));
				}
			}
		 }
	}
}
