package tm.datastructure;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class DocsWordMatrix {
    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> docsWordCountMat = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer,Integer> wordsCount = new ConcurrentHashMap<>();

    public void insertWordCount(int idRef,
                                ConcurrentHashMap<String, Integer> newDocCount,
                                ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> tfIdfMat,
                                ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> normDocsWordCountMat,
                                Set<String> vocab) {

        //Set<String> newVocab = newDocVocab.stream().filter(n -> !vocab.contains(n)).collect(Collectors.toSet());
        Set<String> newVocab = newDocCount.keySet();
        updateVocab(newVocab, newDocCount,vocab);

        //Insert updated dictionary into next row of matrix
        docsWordCountMat.put(idRef, newDocCount);
        //Count max amount of words for the current doc
        //wordsCount.add(getDocWordsCount(newDocCount));

        //Create new row for Tf-Idf matrix
        tfIdfMat.put(idRef, new ConcurrentHashMap<>());
        //Create new row for normalized matrix
        normDocsWordCountMat.put(idRef, new ConcurrentHashMap<>());
    }

    private void updateVocab(final Set<String> newWords, final ConcurrentHashMap<String, Integer> newDocCount, Set<String> vocab) {

        //Update all existing matrix
        docsWordCountMat.forEach((k, doc) -> updateDocVocab(doc, newWords));
        //Update new matrix
        updateDocVocab(newDocCount, vocab);
        //Update vocab to include any new vocab
        vocab.addAll(newWords);
    }

    private void updateDocVocab(ConcurrentHashMap<String, Integer> doc, Set<String> newWords) {
        for (String word : newWords)
            if (!doc.containsKey(word))
                doc.put(word, 0);
    }

}
