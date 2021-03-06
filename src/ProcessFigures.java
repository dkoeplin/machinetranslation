import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Replaces periods in figures as commas and commas to periods within 
* sequences of numbers (i.e 45,67 to 45.67)
*/
public class ProcessFigures {

	Pattern numberPattern = Pattern.compile("\\d+(\\.\\d{3})*(,\\d*)?");
	
	public ProcessFigures() { }
	
	public void applyStrategy(TaggedSentence t) {
	   List<TaggedWord> sentence = t.getSentence();
	   Matcher m;
	   for (int i = 0; i < sentence.size(); i++) {
		   TaggedWord taggedWord = sentence.get(i);
		   String w = taggedWord.word;
		   m = numberPattern.matcher(w);
		   while (m.find()) {
			   if (w.contains(",")) {
				   String[] numbers = w.split(",");
				   numbers[0] = numbers[0].replace(".", ",");
				   sentence.set(i, new TaggedWord(numbers[0] + "." + numbers[1], taggedWord.POS));
			   } else {
				   sentence.set(i, new TaggedWord(w.replace(".", ","), taggedWord.POS));
			   }
		   }
	   }
	}
}
