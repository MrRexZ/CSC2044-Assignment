package tm.datastructure;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class ManhattanSimMatrix {

    private double[][] manSimMatrix;
    public synchronized void buildManSimMatrix(DocsWordMatrix docsWordMatrix, NormsDocWordMatrix normsDocWordMatrix, Set<String> vocab) {
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> docsWordCountMat = docsWordMatrix.docsWordCountMat;
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> normDocsWordCountMat = normsDocWordMatrix.normDocsWordCountMat;
        manSimMatrix = new double[docsWordCountMat.size()][docsWordCountMat.size()];
        normDocsWordCountMat.forEach((fDIn, fDN) ->
                normDocsWordCountMat.forEach((sDIn, sDN) ->
                        vocab.forEach((word) -> manSimMatrix[fDIn][sDIn] += Math.abs(fDN.get(word) - sDN.get(word))
                        )));
    }

    public double[][] getManSimMatrix() {
        return manSimMatrix;
    }
}
