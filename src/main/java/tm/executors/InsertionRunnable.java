package tm.executors;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import tm.datastructure.DocsWordMatrix;
import tm.datastructure.NormsDocWordMatrix;
import tm.datastructure.TfIdfMatrix;
import tm.stemmer.Stemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class InsertionRunnable implements Runnable {

    private int id;
    private DocsWordMatrix docsWordMatrix;
    private TfIdfMatrix tfIdfMatrix;
    private NormsDocWordMatrix normsDocWordMatrix;
    private HashSet<String> stopWordsSet = new HashSet<>();
    private Set<String> vocab;

    public InsertionRunnable(int idRef,
                             HashSet<String> stopWordsSetRef,
                             DocsWordMatrix docsWordMatrixRef,
                             TfIdfMatrix tfIdfMatrixRef,
                             NormsDocWordMatrix normsDocWordMatrixRef,
                             Set<String> vocabRef) {
        id = idRef;
        stopWordsSet = stopWordsSetRef;
        docsWordMatrix = docsWordMatrixRef;
        tfIdfMatrix = tfIdfMatrixRef;
        normsDocWordMatrix = normsDocWordMatrixRef;
        vocab = vocabRef;
    }

    @Override
    public void run() {
        File myFile = new File(String.format("src/main/resources/desc-%d.txt", id));
        ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<String, Integer>();
        PTBTokenizer<CoreLabel> ptbt = null;
        int totalWordCount = 0;
        try {
            ptbt = new PTBTokenizer<CoreLabel>(new FileReader(myFile),
                    new CoreLabelTokenFactory(), "");
            Stemmer s = new Stemmer();
            while (ptbt.hasNext()) {
                CoreLabel label = ptbt.next();
                String word = label.word();

                if (!stopWordsSet.contains(word) && isAWord(word)) {
                    String stemWord = (s.stem(word.toLowerCase()));
                    Integer stemWordCount = wordCount.get(stemWord);
                    wordCount.put(stemWord, stemWordCount == null ? 1 : stemWordCount + 1);
                    totalWordCount++;
                }
            }
            docsWordMatrix.wordsCount.put(id, totalWordCount);
            synchronized (docsWordMatrix) {
                docsWordMatrix.insertWordCount(id, wordCount, tfIdfMatrix.tfIdfMat, normsDocWordMatrix.normDocsWordCountMat, vocab);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isAWord(String word) {
        return !word.replaceAll("[^a-zA-Z ]", "").equals("");
    }
}
