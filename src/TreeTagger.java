import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Simple wrapper for TreeTagger POS tagger
public class TreeTagger {
   String execpath;
   File directory;
 
   Runtime rt = Runtime.getRuntime();
   
   public TreeTagger(String dir, String exec) {
      directory = new File(dir);
      execpath = exec;
   }
   
   public TaggedSentence tagSentence(String sentence) {
      TaggedSentence output = new TaggedSentence();
      try {
         sentence.replaceAll("\"", "\\\"");
         sentence.replaceAll("'", "\\'");
         ProcessBuilder pb = new ProcessBuilder(execpath);
         pb.directory(directory);
         Process tagger = pb.start();
         BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(tagger.getOutputStream()));
         stdOut.write(sentence);
         stdOut.flush();
         BufferedReader stdIn = new BufferedReader(new InputStreamReader(tagger.getInputStream()));

         stdOut.close();
         // read the output from the command
         String s = null;
         while ((s = stdIn.readLine()) != null) {
            String[] tags = s.split("\t");
            String word = tags[0];
            String tag = tags[1];
            int start = sentence.indexOf(word);
            int end   = start + word.length();
            
            output.addWord(sentence.substring(0, start), "");
            if (word.contains(" ")) {
               String [] words = word.split(" ");
               for (int i = 0; i < words.length; i++)
                  output.addWord(words[i], tag);
            }
            else 
               output.addWord(word, tag);
            sentence = sentence.substring(end);
         }
         stdIn.close();
      }
      catch (Exception e) {
         System.out.println(e.getMessage());
         e.printStackTrace();
      }
      return output;
   }
   
}
