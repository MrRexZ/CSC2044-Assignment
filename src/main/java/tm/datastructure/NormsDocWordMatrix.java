package tm.datastructure;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class NormsDocWordMatrix {
    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> normDocsWordCountMat = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>>();

    public synchronized void normalizeDocsMat(DocsWordMatrix docsWordMatrix) {
        normDocsWordCountMat.forEach((k, nDoc) -> {
            docsWordMatrix.docsWordCountMat.get(k).forEach((cDocWord, cDocCount) -> {
                normalizeDoc(docsWordMatrix, k, nDoc, cDocWord, cDocCount);
            });
        });
    }


    private void normalizeDoc(DocsWordMatrix docsWordMatrix, Integer index, ConcurrentHashMap<String, Double> nDoc, String cDocWord, Integer cDocCount) {
        nDoc.put(cDocWord, (double) cDocCount / docsWordMatrix.wordsCount.get(index));
    }
}
