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
				
				TaggedWord previousWord = sentence.get(i-2);
				boolean previous = true;
				if (i > 2) {
					previous = !(previousWord.POS.equals("ADJ") || previousWord.POS.equals("PPX")
							|| previousWord.POS.equals("ART")); 
				}
				if ((!noun && previous && current.POS.equals("VSfin")) 
						|| (!nounAfterComma && comma && !verb && current.POS.equals("VSfin") && previous )) {
					sentence.add(i, new TaggedWord(" ", "space"));
					if (current.isPlural()) sentence.add(i, new TaggedWord("they", "CARD"));
					else sentence.add(i, new TaggedWord("it", "CARD"));
					if (previousWord.POS.equals("ADV") && i > sentence.size() - 2) {
						if (sentence.get(i+2).POS.equals("VSfin")) {
						TaggedWord newPreviousWord = new TaggedWord(sentence.get(i).word, sentence.get(i).POS);
						TaggedWord newCurrentWord = new TaggedWord(sentence.get(i - 2).word, sentence.get(i-2).POS);
						sentence.set(i, newCurrentWord);
						sentence.set(i-2, newPreviousWord);
						/*System.out.println("new previous word: " + newPreviousWord.word);
						System.out.println("new current word: " + newCurrentWord.word);
						System.out.println("THIS APPLIES TOO");*/
						}
					}
					break;
				}
				verb = true;
			}
		}
	}
}
