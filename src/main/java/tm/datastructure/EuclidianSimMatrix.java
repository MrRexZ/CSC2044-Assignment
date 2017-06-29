package tm.datastructure;

import java.util.Set;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class EuclidianSimMatrix {
    private double[][] eucSimMatrix;

    public synchronized void buildEucSimMatrix(DocsWordMatrix docsWordMatrix, NormsDocWordMatrix normsDocWordMatrix, Set<String> vocab) {
        eucSimMatrix = new double[docsWordMatrix.docsWordCountMat.size()][docsWordMatrix.docsWordCountMat.size()];
        normsDocWordMatrix.normDocsWordCountMat.forEach((fDIn, fDN) ->
                normsDocWordMatrix.normDocsWordCountMat.forEach((sDIn, sDN) -> {
                    vocab.forEach((word) -> eucSimMatrix[fDIn][sDIn] += Math.pow(fDN.get(word) - sDN.get(word), 2));
                    eucSimMatrix[fDIn][sDIn] = Math.sqrt(eucSimMatrix[fDIn][sDIn]);
                }));
    }

    public double[][] getEucSimMatrix() {
        return eucSimMatrix;
    }
}
