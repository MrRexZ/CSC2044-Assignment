# CSC2044-Topic Modelling
As part of Sunway University's assignment, the project focuses on developing a 
topic modelling algorithm to determine the similarity of Google Patents descriptions.
It is composed of of several modules such as web crawling, filtering stop words, tokenization,stemming,concurrency,normalizing,
building 3 similarity metrics matrices, and spectral clustering. 
There are 3 main entry-points, in the java class `Crawler`, `Main` and `Clustering`.

## Requirements
1.[Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/)

2.[JSoup](https://jsoup.org/)

3.[smile](https://github.com/haifengl/smile/)

4.[Selenium Web Driver](https://github.com/SeleniumHQ/selenium)

## 1.Web Crawling
The module uses Selenium `Web Driver` and `JSoup`.
To execute this process, run `Crawler` class.
Selenium Web Driver framework is used to start an automated browser process to go to the
patents google website, then to find the input field for query, and then to type in the search
keyword for the keyword “Google”. After that, the crawler attempts to find the search button
by ID and to click on it.
The crawler then attempt to fetches 20 results of patents links, and has the intention to store
them all in an array list, to be passed to the HTML parser class in JSOUP for further parser
processing.
In the current implementation, the crawler successfully retrieves 20 results as required
although there is a weak-point in this implementation, that is reliant on timeout value for
waiting the subsequent page AJAX calls to be successful so that the patents results are
visible.
After retrieving, it sends them to the HTML parser class to perform parsing using JSoup, and
will process the texts description of the patent and print and save it to the resource folder
automatically.
The files will be prefixed by `desc-`, and followed by the iteration identifier.
As an example, I used first 20 documents.


## 2. Tokenizing
The tokenization is based on a slightly modified Penn Treebank Tokenizer algorithm based on Stanford CoreNLP library.
It tokenizes a string into a block of textual units, and regex for word detection is utilized to ensure only words
are extracted, and a stopwords filter is applied as well to prevent the common appearing words to be weighted into calculation.

## 3. Stemming
The stemming uses Porter Stemming Algorithm, taken from Stanford NLP.
The stemming does not yield accurate stem words on some instances (nor does any other
stemming algorithm), but for the objective of this assignment, as long as the multiple types of
words that are to be stemmed to the same words but still stemmed words in improper form,
but still the same base form, is enough as it doesn’t affect the correctness, but merely affects
the visual display to humans.


## 4. Topic Modelling Algorithm
The algorithm implements concurrent processing, in that it reads the output from the HTML
parser and aims to submit each of the document into the cached threadpool.
Each of the thread in the threadpool process the insertion of the patents description topic
modelling. A word count is performed, as well as vocabulary synchronization, and
normalization. Theoretically, vocabulary synchronization across different documents for their
non-appearing words was not necessary as the “matrix” is stored in a concurrenthashmap,
and for non-appearing words in that document, the “get” function performed will return null.
There is also blocking call implemented, to ensure all documents count matrix are built
before they’re used for normalization, tf-idf and the 3 similarity matrices.
After these, a fork-join pool is used on building 2 out of 3 of the similarity matrices (Euclidian
Similarity and Manhattan), while the Cosine Similarity matrix is using a single-threaded
environment due to the performance improvement/effort ratio that is rather low.
The output will be saved as `eucSim.csv` for Euclidian Similarity Matrix, `cosSim.csv` for Cosine Similarity Matrix,
and `manSim.csv` for Manchester Distance Similarity Matrix.

## 5. Spectral Clustering
Spectral Clustering is the clustering methodology used in the assignment, using the smile
framework as described above in the introduction.
The Spectral Clustering performs clustering of the 20 document classes.
Each is assigned to a class by the library, and the coordinates have to be provided manually
to be plotted. The class was encapsulated using Swing GUI library in order to be
displayed.
As a demo, I used Euclidian distance adjacency matrix for the generation of the cluster.