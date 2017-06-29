package tm.datastructure;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class TfIdfMatrix {
    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> tfIdfMat  =  new java.util.concurrent.ConcurrentHashMap<>();

    public synchronized void createTfIdfMat(DocsWordMatrix docsWordMatrix) {
        docsWordMatrix.docsWordCountMat.forEach( (doc,wordCountDict) ->
                wordCountDict.forEach( (word,count) ->
                        tfIdfMat.get(doc).put(word, calculateTfIdf(word, docsWordMatrix))));
    }

    private double calculateTfIdf(String word, DocsWordMatrix docsWordMatrix) {
        int totalDoc = docsWordMatrix.docsWordCountMat.size();
        int docWithTerm = 0;
        for (ConcurrentHashMap<String, Integer> wCountMap : docsWordMatrix.docsWordCountMat.values())
            docWithTerm += wCountMap.contains(word) ? wCountMap.get(word) > 0 ? 1 : 0 : 0;

        return Math.log(totalDoc / (docWithTerm == 0 ? 1 : docWithTerm));
    }
}
