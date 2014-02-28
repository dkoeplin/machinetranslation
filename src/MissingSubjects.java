import java.util.List;


public class MissingSubjects {
	
	public MissingSubjects () {}
	
	void applyStrategy(TaggedSentence s) {
		List<TaggedWord> sentence = s.getSentence();
		boolean noun = false;
		boolean nounAfterComma = false;
		boolean verb = false;
		boolean comma = false;
		
		for (int i = 0; i < sentence.size(); i++) {
			TaggedWord current = sentence.get(i);
			if (current.isNoun()) noun = true;
			if (current.word.equals(",")) comma = true;
			if (comma == true && current.isNoun()) nounAfterComma = true;
			if (current.isVerb()) {
				if (noun == false || (nounAfterComma == false && comma == true && verb == false && current.POS.equals("VLfin"))) {
					sentence.add(i, new TaggedWord(" ", "space"));
					if (current.isPlural()) sentence.add(i, new TaggedWord("they", "CARD"));
					else sentence.add(i, new TaggedWord("it", "CARD"));
					break;
				}
				verb = true;
			}
		}
	}
}
