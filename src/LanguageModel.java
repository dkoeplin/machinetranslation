import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LanguageModel {
   protected Map<String, Double> unigramCounts;
   protected Map<String, Double> bigramLogCounts;
   protected int V;
   protected double logV;
   
   private boolean stupidBackoff = false;

   private String createBigram(String w1, String w2) {
      return w1 + " " + w2;
    }
   
   public LanguageModel(String filename) {
      unigramCounts = new HashMap<String, Double>();
      bigramLogCounts  = new HashMap<String, Double>();
      try {
         BufferedReader input = new BufferedReader( new InputStreamReader(new FileInputStream(filename), "UTF8"));
         for(String line = input.readLine(); line != null; line = input.readLine()) {
            String[] bigram = line.split("\\s+");
            double freq = (double)Integer.valueOf(bigram[0].trim());
            String word1 = bigram[1];
            String word2 = bigram[2];
            bigramLogCounts.put(createBigram(word1, word2), Math.log(freq));
            if (unigramCounts.containsKey(word1))
               unigramCounts.put(word1, unigramCounts.get(word1) + freq);
            else
               unigramCounts.put(word2, freq);
            if (unigramCounts.containsKey(word2))
               unigramCounts.put(word2, unigramCounts.get(word2) + freq);
            else
               unigramCounts.put(word2, freq);
         }
         
         input.close();
      }
      catch(Exception e) {
         System.out.println("Failed while trying to tag sentences");
         e.printStackTrace();
         System.exit(1);
      }
      V = unigramCounts.keySet().size();
      logV = Math.log(V);
   }
   
   public TaggedWord chooseBestTri(TaggedWord prevWord, List<TaggedWord> possibleWords, List<TaggedWord> possibleNexts) {
      if (possibleWords == null || possibleWords.isEmpty())
         return new TaggedWord("UNK", "");
      
      // Default to greedy selection if we have no next word
      if (possibleNexts == null || possibleNexts.isEmpty())
         return chooseBestGreedy(prevWord, possibleWords);
      
      double highScore = Double.NEGATIVE_INFINITY;
      TaggedWord choice = possibleWords.get(0);
      if (possibleWords.size() == 1)
         return choice;
         
      for (TaggedWord cur : possibleWords) {
         for (TaggedWord next : possibleNexts) {
            String[] prevs = prevWord.word.split("\\s+");
            String[] curs = cur.word.split("\\s+");
            String[] nexts = next.word.split("\\s+");

            double thisScore;
            List<String> ngram = new ArrayList<String>();
            ngram.add(prevs[prevs.length - 1]);
            if (curs.length > 1) {
              ngram.add(curs[0]);
              thisScore = score(ngram);
              ngram.clear();
              ngram.add(curs[curs.length - 1]);
              ngram.add(nexts[nexts.length - 1]);
              thisScore += score(ngram);
            }
            else {
               ngram.add(curs[0]);
               ngram.add(nexts[nexts.length - 1]);
               thisScore = score(ngram);
            }
            if (thisScore > highScore) {
               highScore = thisScore;
               choice = cur;
            }
         }
      }
      return choice;
   }
   
   public TaggedWord chooseBestGreedy(TaggedWord prevWord, List<TaggedWord> possibleWords) {
      double highScore = Double.NEGATIVE_INFINITY;
      if (possibleWords.isEmpty()) {
         //System.out.println("No translation available for current word (after " + prevWord.word + ")");
         return new TaggedWord("UNK","");
      }
      TaggedWord choice = possibleWords.get(0);
      if (possibleWords.size() < 2)
         return choice;
      
      for (TaggedWord candidate : possibleWords) {
         String[] previousWord = prevWord.word.split("\\s+");
         String[] candidateWord = candidate.word.split("\\s+");
         
         double thisScore = scorebigram(previousWord[previousWord.length-1], candidateWord[0]);
         //System.out.println("\t" + candidate.word + ": " + score);
         if (thisScore > highScore) {
            highScore = thisScore;
            choice = candidate;
         }
      }
      return choice;
   }
   
   public double scorebigram(String word1, String word2) {
      List<String> bigram = new ArrayList<String>();
      bigram.add(word1);
      bigram.add(word2);
      return score(bigram);
   }
   
   public double score(List<String> sentence) {
      double score = 0.0;
      String prevWord = "<s>";
      for (String word : sentence) {
         if (!word.trim().equals("")) {   // ignore extra spaces
            if (unigramCounts.containsKey(prevWord)) {
               String bigram = createBigram(prevWord, word);
               if (bigramLogCounts.containsKey(bigram))
                  score += bigramLogCounts.get(bigram);  // count(wi-1 wi) add 1 smoothed count for an unseen bigram is 1 (so log is zero)
                     
               score -= Math.log(unigramCounts.get(prevWord) + V); // count(wi-1) + V normalize
            }
            else {
               if (unigramCounts.containsKey(word) && stupidBackoff) {
                  score += Math.log(unigramCounts.get(word));  // count(wi) In stupid backoff we use a lambda on the backoff probability
               }
               score -= logV; 
            }
            prevWord = word;
         }
      }
      return score;
   }
}
