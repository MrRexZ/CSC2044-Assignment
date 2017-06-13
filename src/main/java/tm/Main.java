package tm;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import tm.datastructure.TMMatrix;
import tm.stemmer.Stemmer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */

public class Main {


    public static void main(String[] args) throws IOException {
        //String text = readFile(myFile.toString(), Charset.defaultCharset());

        TMMatrix tmMatrix = new TMMatrix();
        HashSet<String> stopWordsSet = new HashSet<>();
        File stopWordFile = null;

        try {
            stopWordFile = new File(Main.class.getClassLoader().getResource("stopwords/stopwordlist.txt").toURI());
            //Load stopwords
            try (Scanner sc = new Scanner(stopWordFile)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    stopWordsSet.add(line);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < 2; i++) {
            File myFile = new File(String.format("src/main/resources/sample-%d.txt", i));
            HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
            Set<String> newVocab = new HashSet<String>();
            PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new FileReader(myFile),
                    new CoreLabelTokenFactory(), "");
            Stemmer s = new Stemmer();

            while (ptbt.hasNext()) {
                CoreLabel label = ptbt.next();
                String word = label.word();

                if (!stopWordsSet.contains(word)) {
                    String stemWord = (s.stem(word.toLowerCase()));
                    Integer stemWordCount = wordCount.get(stemWord);
                    wordCount.put(stemWord, stemWordCount == null ? 1 : stemWordCount + 1);
                    newVocab.add(stemWord);
                }

            }
            tmMatrix.insertWordCount(newVocab, wordCount);

        }
        tmMatrix.createTfIdfMat();
        tmMatrix.normalizeDocsMat();
        tmMatrix.buildSimMatrix();
        tmMatrix.writeToCSV();
        System.out.println("FINISHED!");
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
