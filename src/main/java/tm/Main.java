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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */

public class Main {


    public static void main(String[] args) throws IOException {
        double avg = 0;
        for (int  x = 0 ; x < 10; x++) {
            //String text = readFile(myFile.toString(), Charset.defaultCharset());
            long startTime = System.nanoTime();
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

            for (int i = 1; i < 3; i++) {
                File myFile = new File(String.format("src/main/resources/d-%d.txt", i));
                ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<String, Integer>();
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
                    }

                }
                tmMatrix.insertWordCount(wordCount);

            }
            tmMatrix.createTfIdfMat();
            tmMatrix.normalizeDocsMat();
            tmMatrix.buildEucSimMatrix();
            tmMatrix.buildManSimMatrix();

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            avg += duration;
            System.out.println("Iteration" + x + " : "+ duration);
            tmMatrix.buildCosSimMatrix();
            tmMatrix.writeToCSV();
            //System.out.println("FINISHED!");
        }
        System.out.println("Average running time : " + avg/10);
    }

    static boolean isAWord(String word) {
        return !word.replaceAll("[^a-zA-Z ]", "").equals("");
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
