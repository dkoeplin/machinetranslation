import java.util.ArrayList;
import java.util.List;


public class RearrangedModifiers {

	public RearrangedModifiers() {}
	
	/* Given a sequence of tagged words with possible sequence of nouns followed
	 * by adjectives (e.g. N1 N2 N3 A1 A2 A3), flips sequence of adjectives and moves
	 * it before the sequence of nouns
	 */
	public void applyStrategy(TaggedSentence s) {
		List<TaggedWord> sentence = s.getSentence();
		
		boolean targetIsNouns = false;
		boolean modifiers = false;
		
		List<TaggedWord> curTarg = new ArrayList<TaggedWord>();
		List<TaggedWord> curMod = new ArrayList<TaggedWord>();
		List<Integer> locs = new ArrayList<Integer>();
		
		for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord current = sentence.get(i);
		   boolean nounAdjStopper = current.isAWord() && targetIsNouns && !current.isAdj() && !current.isNoun();
		   boolean verbAdvStopper = current.isAWord() && !targetIsNouns && !current.isAdv() && !current.isVerb();
		   boolean endOfPhrase = current.isPunct() || i == sentence.size() - 1;
	      // If we've reached a word that is not a correct modifier or a target, a punction mark, or the end of the sentence
         if ((nounAdjStopper || verbAdvStopper || endOfPhrase) && (!curTarg.isEmpty() || !curMod.isEmpty()) ) {
            if (!curTarg.isEmpty() && !curMod.isEmpty()) {
               for (int pos = 0; pos < locs.size(); pos++) {
                  int loc = locs.get(pos);
                  if (pos >= curMod.size())
                     sentence.set(loc, curTarg.get(pos - curMod.size()));
                  else
                     sentence.set(loc, curMod.get(pos));
               }
            }
            curTarg.clear();
            curMod.clear();
            locs.clear();
            modifiers = false;
         }
		   
		   if (!modifiers && (current.isNoun() || current.isVerb()) ) {
		      curTarg.add(current);
		      locs.add(i);
		      targetIsNouns = current.isNoun();
		   }
		   // If we have at least one target word (noun or verb) and this matches the target type
		   else if (!curTarg.isEmpty() && ((current.isAdj() && targetIsNouns) || (current.isAdv() && !targetIsNouns))) {
		      curMod.add(0, current);
		      locs.add(i);
		      modifiers = true;
		   }
		}
	}
}
