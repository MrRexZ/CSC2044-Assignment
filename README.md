# CSC2044-Topic Modelling
Sunway University's Assignment on building a topic modelling algorithm.
The project is composed of filtering stop words,tokenization,stemming,analyzing and synchronizing word counts 
across document,normalizing, building 3 similarity metrics matrices.
The project makes use of [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/) and [JSoup](https://jsoup.org/)

##1. Stopwords filtering
 First process performed is to filter all of the common stop words, based on
 the list of the website here

##2. Tokenizing
The tokenization is based on a slightly modified Penn 
Treebank Tokenizer algorithm based on Stanford CoreNLP library.
It tokenizes a string into a block of textual units.
The implementation doesn't disregard punctuations and possessives, and is processed 
into the matrix, allowing it to contributes to both semantic and morphological similarity
between the documents.

##3. Stemming
Stemming is performed using a class taken from StanfordCore NLP library
using Porter Stemming Algorithm.

##MORE EDITS TO README INCOMING, CURRENTLY BUSY WITH OTHER PROJECTS
