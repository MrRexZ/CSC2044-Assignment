package tm;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import tm.cvs.CSVUtils;
import tm.datastructure.*;
import tm.executors.InsertionRunnable;
import tm.stemmer.Stemmer;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */

public class Main {

    static AtomicInteger nDocs = new AtomicInteger();

    public static void main(String[] args) throws IOException {
        double avg = 0;
        //String text = readFile(myFile.toString(), Charset.defaultCharset());
        long startTime = System.nanoTime();

        //Initialization
        DocsWordMatrix docsWordMatrix = new DocsWordMatrix();
        NormsDocWordMatrix normsDocWordMatrix = new NormsDocWordMatrix();
        TfIdfMatrix tfIdfMatrix = new TfIdfMatrix();
        EuclidianSimMatrix euclidianSimMatrix = new EuclidianSimMatrix();
        ManhattanSimMatrix manhattanSimMatrix = new ManhattanSimMatrix();
        CosineSimMatrix cosineSimMatrix = new CosineSimMatrix();
        HashSet<String> stopWordsSet = new HashSet<>();
        Set<String> vocab = ConcurrentHashMap.newKeySet(0);
        File stopWordFile = null;

        try {
            stopWordFile = new File(Main.class.getClassLoader().getResource("stopwords/stopwordlist.txt").toURI());
            //Load stopwords
            try (Scanner sc = new Scanner(stopWordFile)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    stopWordsSet.add(line);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ExecutorService docsExecutors = Executors.newCachedThreadPool();

        List<Future> insertionRunnables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            nDocs.incrementAndGet();
            insertionRunnables.add(docsExecutors.submit(new InsertionRunnable(i, stopWordsSet, docsWordMatrix, tfIdfMatrix, normsDocWordMatrix, vocab)));
        }
        for (Future<?> future:insertionRunnables) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        tfIdfMatrix.createTfIdfMat(docsWordMatrix);
        normsDocWordMatrix.normalizeDocsMat(docsWordMatrix);
        euclidianSimMatrix.buildEucSimMatrix(docsWordMatrix ,normsDocWordMatrix, vocab);
        manhattanSimMatrix.buildManSimMatrix(docsWordMatrix,normsDocWordMatrix,vocab);
        cosineSimMatrix.buildCosSimMatrix(docsWordMatrix,normsDocWordMatrix,vocab);
        writeToCSV(euclidianSimMatrix.getEucSimMatrix(), cosineSimMatrix.getCosSimMatrix(), manhattanSimMatrix.getManSimMatrix());
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        avg += duration;
        System.out.println("FINISHED!");
    }




    static synchronized void writeToCSV(double[][] eucSimMatrix, double[][] cosSimMatrix, double[][] manSimMatrix) {
        createCSV(eucSimMatrix, "src/main/resources/eucSim.csv");
        createCSV(cosSimMatrix,"src/main/resources/cosSim.csv");
        createCSV(manSimMatrix, "src/main/resources/manSim.csv");
    }

    static void createCSV(double[][] fMatrix, String outPath) {
        try (PrintWriter out = new PrintWriter(String.format(outPath))) {
            for(int row = 0 ; row < nDocs.get() ; row++) {
                List<String> rowSimMatrix = new ArrayList<String>();
                for (int col = 0 ; col < nDocs.get() ; col++){
                    rowSimMatrix.add(String.valueOf(fMatrix[row][col]));
                }
                CSVUtils.writeLine(out, rowSimMatrix);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
