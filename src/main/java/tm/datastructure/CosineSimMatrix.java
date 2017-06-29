package tm.datastructure;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class CosineSimMatrix {
    private double[][] cosSimMatrix;

    public synchronized void buildCosSimMatrix(DocsWordMatrix docsWordMatrix, NormsDocWordMatrix normsDocWordMatrix, Set<String> vocab) {

        cosSimMatrix = new double[docsWordMatrix.docsWordCountMat.size()][docsWordMatrix.docsWordCountMat.size()];
        for (Map.Entry<Integer, ConcurrentHashMap<String, Double>> fDoc : normsDocWordMatrix.normDocsWordCountMat.entrySet())
            for (Map.Entry<Integer, ConcurrentHashMap<String, Double>> sDoc : normsDocWordMatrix.normDocsWordCountMat.entrySet()) {
                int fDocIndex = fDoc.getKey();
                int sDocIndex = sDoc.getKey();
                double numerator = 0;
                double fDoc_d = 0;
                double sDoc_d = 0;
                for (String word : vocab) {
                    double fDoc_nVal = fDoc.getValue().get(word);
                    double sDoc_nVal = sDoc.getValue().get(word);
                    numerator += fDoc_nVal * sDoc_nVal;
                    fDoc_d += fDoc_nVal * fDoc_nVal;
                    sDoc_d += sDoc_nVal * sDoc_nVal;
                }
                cosSimMatrix[fDocIndex][sDocIndex] = numerator / (Math.sqrt(fDoc_d) * Math.sqrt(sDoc_d));
            }
    }

    public double[][] getCosSimMatrix() {
        return cosSimMatrix;
    }
}
