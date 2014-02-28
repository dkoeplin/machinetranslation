import java.util.List;


public class ProcessNegation {
	
	public ProcessNegation() {}
	
	public void applyStrategy(TaggedSentence t) {
		noBeforeVerb(t);   
	   /*List<TaggedWord> sentence = t.getSentence();
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
		   }*/
	}
	
	public void noBeforeVerb(TaggedSentence t) {
      List<TaggedWord> sentence = t.getSentence();
      for (int i = 0; i < sentence.size(); i++) {
         TaggedWord w = sentence.get(i);
         
         if (w.POS.equals("NEG") && i < sentence.size()-2) {
            TaggedWord nextWord = sentence.get(i+2);
            if (nextWord.POS.equals("VSfin") || nextWord.POS.equals("VEfin") || nextWord.POS.equals("VMfin")) {
               TaggedWord current = new TaggedWord(nextWord.word, nextWord.POS);
               TaggedWord next = new TaggedWord("not", w.POS);
               sentence.set(i, current);
               sentence.set(i+ 2, next);
             
               //System.out.println("VSfin: " + nextWord.word);
            } else if (nextWord.POS.equals("VLfin")) {
                   //Do-support: when the word following negation is not an auxiliary verb, we insert the verb “do”
                   //with the new POS tag “VDfin”
                   TaggedWord doNot = new TaggedWord("do not", "VDfin");
               sentence.set(i, doNot);
               }
         }
      }
   }
}
