package tm.datastructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import tm.cvs.CSVUtils;

/**
 * Created by Anthony Tjuatja on 6/7/2017.
 */


public class TMMatrix {

    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> docsWordCountMat = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> tfIdfMat         =  new ConcurrentHashMap<>();
    private ArrayList<Integer> wordsCount = new ArrayList<Integer>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>> normDocsWordCountMat = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>>();
    //private INDArray normDocsMat;
    private Set<String> vocab = ConcurrentHashMap.newKeySet(0);

    private double[][] eucSimMatrix;
    private double[][] cosSimMatrix;


    public TMMatrix() {

    }

    public synchronized void insertWordCount(Set<String> newVocab, ConcurrentHashMap<String, Integer> newDocCount) {

        //Set<String> newVocab = newDocVocab.stream().filter(n -> !vocab.contains(n)).collect(Collectors.toSet());
        ConcurrentHashMap<String, Integer> updatedNewDocCount = newDocCount;
        updateVocab(newVocab, updatedNewDocCount);

        //Insert updated dictionary into next row of matrix
        docsWordCountMat.put(docsWordCountMat.size(), updatedNewDocCount);
        //Count max amount of words for the current doc
        wordsCount.add(getDocWordsCount(newDocCount));


        //Create new row for Tf-Idf matrix
        tfIdfMat.put(tfIdfMat.size(), new ConcurrentHashMap<>());
        //Create new row for normalized matrix
        normDocsWordCountMat.put(normDocsWordCountMat.size(), new ConcurrentHashMap<>());


    }

    private Integer getDocWordsCount(ConcurrentHashMap<String, Integer> newDocCount) {
        Integer wCount = 0;
        for (Map.Entry<String, Integer> doc : newDocCount.entrySet())
            wCount += doc.getValue();
        return wCount;
    }

    public synchronized void createTfIdfMat() {
        docsWordCountMat.forEach( (doc,wordCountDict) ->
                wordCountDict.forEach( (word,count) ->
                        tfIdfMat.get(doc).put(word, calculateTfIdf(word))));
    }

    private double calculateTfIdf(String word) {
        int totalDoc = docsWordCountMat.size();
        int docWithTerm = 0;
        for (ConcurrentHashMap<String, Integer> wCountMap : docsWordCountMat.values())
            docWithTerm += wCountMap.get(word) > 0 ? 1 : 0;

        return Math.log(totalDoc / (docWithTerm == 0 ? 1 : docWithTerm));
    }

    private void updateVocab(final Set<String> newWords, final ConcurrentHashMap<String, Integer> newDocCount) {

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

    public synchronized void normalizeDocsMat() {
        normDocsWordCountMat.forEach((k, nDoc) -> {
            docsWordCountMat.get(k).forEach((cDocWord, cDocCount) -> {
                normalizeDoc(k, nDoc, cDocWord, cDocCount);
            });
        });
    }


    private void normalizeDoc(Integer index, ConcurrentHashMap<String, Double> nDoc, String cDocWord, Integer cDocCount) {
        nDoc.put(cDocWord, (double) cDocCount / wordsCount.get(index));
    }

    public synchronized void buildEucSimMatrix() {

        eucSimMatrix = new double[docsWordCountMat.size()][docsWordCountMat.size()];
        normDocsWordCountMat.forEach((fDIn, fDN) -> normDocsWordCountMat.forEach((sDIn, sDN) -> {
            vocab.forEach((word) -> eucSimMatrix[fDIn][sDIn] += Math.pow(fDN.get(word) - sDN.get(word), 2));
            eucSimMatrix[fDIn][sDIn] = Math.sqrt(eucSimMatrix[fDIn][sDIn]);
        }));
    }

    public synchronized void buildCosSimMatrix() {

        cosSimMatrix = new double[docsWordCountMat.size()][docsWordCountMat.size()];
        for (Map.Entry<Integer, ConcurrentHashMap<String, Double>> fDoc : normDocsWordCountMat.entrySet())
            for (Map.Entry<Integer, ConcurrentHashMap<String, Double>> sDoc : normDocsWordCountMat.entrySet()) {
                int fDocIndex = fDoc.getKey();
                int sDocIndex = sDoc.getKey();
                double numerator = 0;
                double fDoc_d = 0;
                double sDoc_d = 0;
                for (String word : vocab){
                    double fDoc_nVal = fDoc.getValue().get(word);
                    double sDoc_nVal = sDoc.getValue().get(word);
                    numerator +=  fDoc_nVal * sDoc_nVal;
                    fDoc_d += fDoc_nVal * fDoc_nVal;
                    sDoc_d += sDoc_nVal * sDoc_nVal;
                }
                cosSimMatrix[fDocIndex][sDocIndex] = numerator / (Math.sqrt(fDoc_d) * Math.sqrt(sDoc_d));
            }
    }

    public synchronized void writeToCSV() {
    	try (PrintWriter out = new PrintWriter(String.format("src/main/resources/test.csv"))) {
	    	for(int row = 0 ; row < docsWordCountMat.size() ; row++) {
		    	List<String> rowSimMatrix = new ArrayList<String>();
		    	for (int col = 0 ; col < docsWordCountMat.size() ; col++){
		    		rowSimMatrix.add(String.valueOf(eucSimMatrix[row][col]));
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
    

    
}
