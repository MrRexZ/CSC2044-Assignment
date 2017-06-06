package tm.datastructure;

import java.util.*;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */


public class TMMatrix {

    private HashMap<Integer, HashMap<String, Integer>> docsWordCountMat = new HashMap<>();
    private ArrayList<Integer> wordsCount = new ArrayList<>();
    private HashMap<Integer, HashMap<String, Float>> normDocsWordCountMat = new HashMap<>();
    //private INDArray normDocsMat;
    private Set<String> vocab = new HashSet<>();
    private double[][] simMatrix;


    public TMMatrix() {

    }

    public synchronized void insertWordCount(Set<String> newVocab, HashMap<String, Integer> newDocCount) {

        //Set<String> newVocab = newDocVocab.stream().filter(n -> !vocab.contains(n)).collect(Collectors.toSet());
        HashMap<String, Integer> updatedNewDocCount = newDocCount;
        updateVocab(newVocab, updatedNewDocCount);

        //Insert updated dictionary into next row of matrix
        docsWordCountMat.put(docsWordCountMat.size(), updatedNewDocCount);
        //Count max amount of words for the current row
        wordsCount.add(getDocWordsCount(newDocCount));


        //Create new row for normalized matrix
        normDocsWordCountMat.put(normDocsWordCountMat.size(), new HashMap<>());


    }

    private Integer getDocWordsCount(HashMap<String, Integer> newDocCount) {
        Integer wCount = 0;

        for (Map.Entry<String, Integer> doc : newDocCount.entrySet()) {
            wCount += doc.getValue();
        }

        return wCount;
    }

    private void updateVocab(final Set<String> newWords, final HashMap<String, Integer> newDocCount) {

        //Update all existing matrix
        docsWordCountMat.forEach((k, doc) -> updateDocVocab(doc, newWords));
        //Update new matrix
        updateDocVocab(newDocCount, vocab);
        //Update vocab to include any new vocab
        vocab.addAll(newWords);
    }

    private void updateDocVocab(HashMap<String, Integer> doc, Set<String> newWords) {
        for (String word : newWords)
            if (!doc.containsKey(word))
                doc.put(word, 0);

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

    public synchronized void buildSimMatrix() {
        simMatrix = new double[docsWordCountMat.size()][docsWordCountMat.size()];
        normDocsWordCountMat.forEach((fDIn, fDN) -> normDocsWordCountMat.forEach((sDIn, sDN) -> {
            vocab.forEach((word) -> simMatrix[fDIn][sDIn] += Math.pow(fDN.get(word) - sDN.get(word), 2));
            simMatrix[fDIn][sDIn] = Math.sqrt(simMatrix[fDIn][sDIn]);
        }));

    }

}
