package tm.datastructure;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class EuclidianSimMatrix {
    private double[][] eucSimMatrix;

    public synchronized void buildEucSimMatrix(DocsWordMatrix docsWordMatrix, NormsDocWordMatrix normsDocWordMatrix, Set<String> vocab) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        Future task = forkJoinPool.submit(() -> {
            eucSimMatrix = new double[docsWordMatrix.docsWordCountMat.size()][docsWordMatrix.docsWordCountMat.size()];
            normsDocWordMatrix.normDocsWordCountMat.forEach(4,(fDIn, fDN) ->
                    normsDocWordMatrix.normDocsWordCountMat.forEach(4, (sDIn, sDN) -> {
                        vocab.parallelStream().forEach((word) -> eucSimMatrix[fDIn][sDIn] += Math.pow(fDN.get(word) - sDN.get(word), 2));
                        eucSimMatrix[fDIn][sDIn] = Math.sqrt(eucSimMatrix[fDIn][sDIn]);
                    }));
        });
        task.get();
    }


    public double[][] getEucSimMatrix() {
        return eucSimMatrix;
    }
}
