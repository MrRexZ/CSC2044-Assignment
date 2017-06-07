package tm;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import tm.datastructure.TMMatrix;
import tm.stemmer.Stemmer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */

public class Main {


    public static void main(String[] args) throws IOException {
        //String text = readFile(myFile.toString(), Charset.defaultCharset());

        TMMatrix tmMatrix = new TMMatrix();

        for (int i = 0; i < 20; i++) {
            File myFile = new File(String.format("src/main/resources/desc-%d.txt", i));
            HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
            Set<String> newVocab = new HashSet<String>();
            PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new FileReader(myFile),
                    new CoreLabelTokenFactory(), "");
            Stemmer s = new Stemmer();

            while (ptbt.hasNext()) {
                CoreLabel label = ptbt.next();
                String stemWord = (label.word().toLowerCase());
                Integer stemWordCount = wordCount.get(s.stem(stemWord));
                wordCount.put(stemWord, stemWordCount == null ? 1 : stemWordCount + 1);
                newVocab.add(stemWord);

            }
            tmMatrix.insertWordCount(newVocab, wordCount);

        }

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
