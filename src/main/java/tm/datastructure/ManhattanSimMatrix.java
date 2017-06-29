package tm.datastructure;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class ManhattanSimMatrix {
    private double[][] manSimMatrix;

    public synchronized void buildManSimMatrix(DocsWordMatrix docsWordMatrix, NormsDocWordMatrix normsDocWordMatrix, Set<String> vocab) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        Future task = forkJoinPool.submit(() -> {
            ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> docsWordCountMat = docsWordMatrix.docsWordCountMat;
            ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> normDocsWordCountMat = normsDocWordMatrix.normDocsWordCountMat;
            manSimMatrix = new double[docsWordCountMat.size()][docsWordCountMat.size()];
            normDocsWordCountMat.forEach(4,(fDIn, fDN) ->
                    normDocsWordCountMat.forEach(4,(sDIn, sDN) ->
                            vocab.parallelStream().forEach((word) -> manSimMatrix[fDIn][sDIn] += Math.abs(fDN.get(word) - sDN.get(word))
                            )));
        });
        task.get();
    }


    public double[][] getManSimMatrix() {
        return manSimMatrix;
    }
}
