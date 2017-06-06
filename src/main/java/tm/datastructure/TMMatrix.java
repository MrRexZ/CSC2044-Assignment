package tm.datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */


public class TMMatrix {

    private HashMap<Integer, HashMap<String, Integer>> docsWordCountMat = new HashMap<>();
    private ArrayList<Integer> wordsCount = new ArrayList<>();
    private HashMap<Integer, HashMap<String, Float>> normDocsWordCountMat = new HashMap<>();

    public TMMatrix() {

    }

    public synchronized void insertWordCount(Set<String> newWords, HashMap<String, Integer> newDocCount) {
        docsWordCountMat.put(docsWordCountMat.size(), newDocCount);
        wordsCount.add(getDocWordsCount(newDocCount));
        normDocsWordCountMat.put(normDocsWordCountMat.size(), new HashMap<>());
        updateExistingWordCount(newWords);

    }

    private Integer getDocWordsCount(HashMap<String, Integer> newDocCount) {
        Integer wCount = 0;

        for (Map.Entry<String, Integer> doc : newDocCount.entrySet()) {
            wCount += doc.getValue();
        }

        return wCount;
    }

    private void updateExistingWordCount(final Set<String> newWords) {
        docsWordCountMat.forEach((k, doc) -> updateDocVocab(doc, newWords));
    }

    private void updateDocVocab(HashMap<String, Integer> doc, Set<String> newWords) {
        for (String word : newWords) {
            doc.put(word, 0);
        }
    }

    public synchronized void normalizeDocsMat() {
        normDocsWordCountMat.forEach((k, nDoc) -> {
            docsWordCountMat.get(k).forEach((cDocWord, cDocCount) -> {
                normalizeDoc(k, nDoc, cDocWord, cDocCount);
            });
        });
    }

    private void normalizeDoc(Integer index, HashMap<String, Float> nDoc, String cDocWord, Integer cDocCount) {
        nDoc.put(cDocWord, (float) cDocCount / wordsCount.get(index));
    }

}
