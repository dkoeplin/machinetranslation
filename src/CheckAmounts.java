import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* This strategy finds and replaces occurrences in the English translation that look like
 *  "# million(s)/thousand(s) of" and replaces them with "# million/thousand".
 * Additionally, if the number before thousand/million ends in ",000", we up the number
 * to million/billion (respectively) and remove the ",000"
 */
public class CheckAmounts {

	public CheckAmounts() {}
	
	Pattern overThousand = Pattern.compile(",000");   // only up it if this is exactly a multiple of one thousand
	Pattern amount = Pattern.compile("\\d");
	
	public void applyStrategy(TaggedSentence t) {
		List<TaggedWord> sentence = t.getSentence();
		for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord w = sentence.get(i);
		   String word = w.word.toLowerCase();
			if ((word.startsWith("million") || word.startsWith("thousand")) && i < sentence.size() - 2 && i > 1) {
			   if (sentence.get(i+2).word.equals("of")) {
			      TaggedWord value = sentence.get(i - 2);
			      Matcher m = amount.matcher(value.word);
			      boolean thousandMultiple = value.word.endsWith(",000");
					if (m.find()) {
   			      String english;
   					if (word.toLowerCase().startsWith("million")) {
   					   if (thousandMultiple) 
   					      english = "billion";
   					   else
   					      english = "million";
   					}
   					else {
   					   if (thousandMultiple)
   					      english = "million";
   					   else
   					      english = "thousand";
   					}
   					if (thousandMultiple) {
   					   String newValue = value.word.substring(0, value.word.length() - 4);
   					   sentence.set(i - 2, new TaggedWord(newValue, w.POS, w.tense));
   					}
   					sentence.set(i, new TaggedWord(english, w.POS));
   					sentence.remove(i+1);
   					sentence.remove(i+1);
					}
			   }
			}
		}
	}
}
