package org.latinolib.bow;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.latinolib.VectorEntry;
import org.latinolib.model.ModelUtils;
import org.latinolib.SparseVector;
import org.latinolib.stemmer.Stemmer;
import org.latinolib.stopwords.StopWords;
import org.latinolib.tokenizer.SimpleTokenizer;
import org.latinolib.tokenizer.SimpleTokenizerType;
import org.latinolib.tokenizer.Token;
import org.latinolib.tokenizer.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Author saxo
 */
public class BowSpace implements Serializable
{
    private static final long serialVersionUID = 7695534861034247430L;

    private transient Logger logger = LoggerFactory.getLogger(BowSpace.class);

    private Tokenizer tokenizer = new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS);
    private StopWords stopWords = null;
    private Stemmer stemmer = null;
    private Map<String, Word> wordInfo = new HashMap<String, Word>();
    private List<Word> idxInfo = Lists.newArrayList();
    private int maxNGramLen = 2;
    private int minWordFreq = 5;
    private WordWeightType wordWeightType = WordWeightType.TERM_FREQ;
    private double cutLowWeightsPerc = 0.2;
    private boolean normalizeVectors = true;
    private boolean keepWordForms = false;

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = Preconditions.checkNotNull(tokenizer);
    }

    public StopWords getStopWords() {
        return stopWords;
    }

    public void setStopWords(StopWords stopWords) {
        this.stopWords = stopWords;
    }

    public Stemmer getStemmer() {
        return stemmer;
    }

    public void setStemmer(Stemmer stemmer) {
        this.stemmer = stemmer;
    }

    public Map<String, Word> getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(Map<String, Word> wordInfo) {
        this.wordInfo = wordInfo;
    }

    public List<Word> getIdxInfo() {
        return Collections.unmodifiableList(idxInfo);
    }

    public int getMaxNGramLen() {
        return maxNGramLen;
    }

    public void setMaxNGramLen(int maxNGramLen) {
        Preconditions.checkArgument(maxNGramLen >= 1);
        this.maxNGramLen = maxNGramLen;
    }

    public int getMinWordFreq() {
        return minWordFreq;
    }

    public void setMinWordFreq(int minWordFreq) {
        Preconditions.checkArgument(minWordFreq >= 1);
        this.minWordFreq = minWordFreq;
    }

    public WordWeightType getWordWeightType() {
        return wordWeightType;
    }

    public void setWordWeightType(WordWeightType wordWeightType) {
        this.wordWeightType = wordWeightType;
    }

    public double getCutLowWeightsPerc() {
        return cutLowWeightsPerc;
    }

    public void setCutLowWeightsPerc(double cutLowWeightsPerc) {
        Preconditions.checkArgument(cutLowWeightsPerc >= 0 && cutLowWeightsPerc < 1);
        this.cutLowWeightsPerc = cutLowWeightsPerc;
    }

    public boolean isNormalizeVectors() {
        return normalizeVectors;
    }

    public void setNormalizeVectors(boolean normalizeVectors) {
        this.normalizeVectors = normalizeVectors;
    }

    public boolean isKeepWordForms() {
        return keepWordForms;
    }

    public void setKeepWordForms(boolean keepWordForms) {
        this.keepWordForms = keepWordForms;
    }

    private void processNGramsPass1(List<WordStem> nGrams, int startIdx, Set<String> docWords) {
        String nGramStem = "";
        String nGram = "";
        for (int i = startIdx; i < nGrams.size(); i++) {
            nGram += nGrams.get(i).word;
            nGramStem += nGrams.get(i).stem;
            if (!wordInfo.containsKey(nGramStem)) {
                Word nGramInfo = new Word(nGram, nGramStem);
                wordInfo.put(nGramStem, nGramInfo);
                docWords.add(nGramStem);
            } else {
                Word nGramInfo = wordInfo.get(nGramStem);
                if (!docWords.contains(nGramStem)) {
                    docWords.add(nGramStem);
                    nGramInfo.docFreq++;
                }
                nGramInfo.freq++;
                Integer freq = nGramInfo.forms.get(nGram);
                nGramInfo.forms.put(nGram, freq == null ? 1 : ++freq);
            }
            nGram += " ";
            nGramStem += " ";
        }
    }

    private void processNGramsPass2(List<WordStem> nGrams, int startIdx, Map<Integer, Integer> tfVec) {
        String nGramStem = "";
        for (int i = startIdx; i < nGrams.size(); i++) {
            nGramStem += nGrams.get(i).stem;
            if (wordInfo.containsKey(nGramStem)) {
                Word wordInfo = this.wordInfo.get(nGramStem);
                if (wordInfo.idx == -1) {
                    wordInfo.idx = idxInfo.size();
                    tfVec.put(wordInfo.idx, 1);
                    idxInfo.add(wordInfo);
                } else {
                    Integer tfVal = tfVec.get(wordInfo.idx);
                    tfVec.put(wordInfo.idx, tfVal == null ? 1 : ++tfVal);
                }
            } else {
                break;
            }
            nGramStem += " ";
        }
    }

    public List<SparseVector> initialize(Iterable<String> documents) {
        return initialize(documents, true, false);
    }

    private String normalizeText(String text, boolean doNormalize) {
        return doNormalize ? text.trim().toLowerCase() : text;
    }

    public List<SparseVector> initialize(Iterable<String> documents, boolean normalizeTokens, boolean largeScale) {
        Preconditions.checkNotNull(documents);

        wordInfo.clear();
        idxInfo.clear();
        List<SparseVector> bows = Lists.newArrayList();

        // build vocabulary
        logger.debug("Building vocabulary ...");
        int docCount = 0;
        if (!largeScale) {
            for (String document : documents) {
                docCount++;
                logger.debug("Document {} ...", docCount);
                Set<String> docWords = new HashSet<String>();
                List<WordStem> nGrams = Lists.newArrayListWithCapacity(maxNGramLen);
                for (Token token : tokenizer.getTokens(document)) {
                    String word = normalizeText(token.getText(), normalizeTokens);
                    if (stopWords == null || !stopWords.isStopWord(word)) {
                        String stem = stemmer == null ? word : normalizeText(stemmer.getStem(word), normalizeTokens);
                        if (nGrams.size() < maxNGramLen) {
                            WordStem wordStem = new WordStem();
                            wordStem.word = word;
                            wordStem.stem = stem;
                            nGrams.add(wordStem);
                            if (nGrams.size() < maxNGramLen) {
                                continue;
                            }
                        } else {
                            WordStem wordStem = nGrams.get(0);
                            wordStem.word = word;
                            wordStem.stem = stem;
                            for (int i = 0; i < maxNGramLen - 1; i++) {
                                nGrams.set(i, nGrams.get(i + 1));
                            }
                            nGrams.set(maxNGramLen - 1, wordStem);
                        }
                        processNGramsPass1(nGrams, 0, docWords);
                    }
                }
                int startIdx = nGrams.size() == maxNGramLen ? 1 : 0;
                for (int i = startIdx; i < nGrams.size(); i++) {
                    processNGramsPass1(nGrams, i, docWords);
                }
            }
            logger.debug("Document {} ...", docCount);
        } else // large-scale mode (needs less memory, slower)
        {
            for (int n = 1; n <= maxNGramLen; n++) {
                docCount = 0;
                logger.debug("Pass {} of {} ...", n, maxNGramLen);
                for (String document : documents) {
                    docCount++;
                    logger.debug("initialize", "Document {} ...", docCount);
                    List<WordStem> nGrams = Lists.newArrayListWithCapacity(n);
                    Set<String> docWords = new HashSet<String>();
                    for (Token token : tokenizer.getTokens(document)) {
                        String word = normalizeText(token.getText(), normalizeTokens);
                        if (stopWords == null || !stopWords.isStopWord(word)) {
                            String stem = stemmer == null ? word : normalizeText(stemmer.getStem(word), normalizeTokens);
                            if (nGrams.size() < n) {
                                WordStem wordStem = new WordStem();
                                wordStem.word = word;
                                wordStem.stem = stem;
                                nGrams.add(wordStem);
                                if (nGrams.size() < n) {
                                    continue;
                                }
                            } else {
                                WordStem wordStem = nGrams.get(0);
                                wordStem.word = word;
                                wordStem.stem = stem;
                                for (int i = 0; i < n - 1; i++) {
                                    nGrams.set(i, nGrams.get(i + 1));
                                }
                                nGrams.set(n - 1, wordStem);
                            }
                            String nGram = nGrams.get(0).word;
                            String nGramStem = nGrams.get(0).stem;
                            if (n > 1) {
                                for (int i = 1; i < n - 1; i++) {
                                    nGram += " " + nGrams.get(i).word;
                                    nGramStem += " " + nGrams.get(i).stem;
                                }
                                if (!wordInfo.containsKey(nGramStem)) {
                                    continue;
                                }
                                if (wordInfo.get(nGramStem).freq < minWordFreq) {
                                    continue;
                                }
                                String nGramStem2 = "";
                                for (int i = 1; i < n - 1; i++) {
                                    nGramStem2 += nGrams.get(i).stem + " ";
                                }
                                nGramStem2 += nGrams.get(n - 1).stem;
                                if (!wordInfo.containsKey(nGramStem2)) {
                                    continue;
                                }
                                if (wordInfo.get(nGramStem2).freq < minWordFreq) {
                                    continue;
                                }
                                nGram += " " + nGrams.get(n - 1).word;
                                nGramStem += " " + nGrams.get(n - 1).stem;
                            }
                            if (!wordInfo.containsKey(nGramStem)) {
                                Word nGramInfo = new Word(nGram, nGramStem);
                                wordInfo.put(nGramStem, nGramInfo);
                                docWords.add(nGramStem);
                            } else {
                                Word nGramInfo = wordInfo.get(nGramStem);
                                if (!docWords.contains(nGramStem)) {
                                    nGramInfo.docFreq++;
                                    docWords.add(nGramStem);
                                }
                                nGramInfo.freq++;
                                Integer count = nGramInfo.forms.get(nGram);
                                nGramInfo.forms.put(nGram, count == null ? 1 : ++count);
                            }
                        }
                    }
                }
                logger.debug("Document {} ...", docCount);
            }
        }
        // remove unfrequent words and n-grams, precompute IDF
        List<String> removeList = Lists.newArrayList();
        for (Map.Entry<String, Word> info : wordInfo.entrySet()) {
            if (info.getValue().freq < minWordFreq) {
                removeList.add(info.getKey());
            } else {
                info.getValue().idf = Math.log((double) docCount / (double) info.getValue().docFreq);
            }
        }
        for (String key : removeList) {
            wordInfo.remove(key);
        }

        // determine most frequent word and n-gram forms
        for (Word info : wordInfo.values()) {
            int max = 0;
            for (Map.Entry<String, Integer> wordForm : info.forms.entrySet()) {
                if (wordForm.getValue() > max) {
                    max = wordForm.getValue();
                    info.mostFrequentForm = wordForm.getKey();
                }
            }
            if (!keepWordForms) {
                info.forms.clear();
            }
        }

        // compute bag-of-words vectors
        logger.debug("Computing bag-of-words vectors ...");
        int docNum = 1;
        for (String document : documents) {
            logger.debug("initialize", "Document {} / {} ...", docNum++, docCount);
            Map<Integer, Integer> tfVec = new HashMap<Integer, Integer>();
            List<WordStem> nGrams = Lists.newArrayListWithCapacity(maxNGramLen);
            for (Token token : tokenizer.getTokens(document)) {
                String word = normalizeText(token.getText(), normalizeTokens);
                if (stopWords == null || !stopWords.isStopWord(word)) {
                    String stem = stemmer == null ? word : normalizeText(stemmer.getStem(word), normalizeTokens);
                    if (nGrams.size() < maxNGramLen) {
                        WordStem wordStem = new WordStem();
                        wordStem.word = word;
                        wordStem.stem = stem;
                        nGrams.add(wordStem);
                        if (nGrams.size() < maxNGramLen) {
                            continue;
                        }
                    } else {
                        WordStem wordStem = nGrams.get(0);
                        wordStem.word = word;
                        wordStem.stem = stem;
                        for (int i = 0; i < maxNGramLen - 1; i++) {
                            nGrams.set(i, nGrams.get(i + 1));
                        }
                        nGrams.set(maxNGramLen - 1, wordStem);
                    }
                    processNGramsPass2(nGrams, 0, tfVec);
                }
            }
            int startIdx = nGrams.size() == maxNGramLen ? 1 : 0;
            for (int i = startIdx; i < nGrams.size(); i++) {
                processNGramsPass2(nGrams, i, tfVec);
            }
            SparseVector docVec = new SparseVector(0);
            switch (wordWeightType) {
                case TERM_FREQ:
                    for (Map.Entry<Integer, Integer> tfItem : tfVec.entrySet()) {
                        docVec.add(tfItem.getKey(), tfItem.getValue());
                    }
                    break;
                case TF_IDF:
                    for (Map.Entry<Integer, Integer> tfItem : tfVec.entrySet()) {
                        double tfIdf = (double) tfItem.getValue() * idxInfo.get(tfItem.getKey()).idf;
                        if (tfIdf > 0) {
                            docVec.add(tfItem.getKey(), tfIdf);
                        }
                    }
                    break;
                case LOG_DF_TF_IDF:
                    for (Map.Entry<Integer, Integer> tfItem : tfVec.entrySet()) {
                        double tfIdf = (double) tfItem.getValue() * idxInfo.get(tfItem.getKey()).idf;
                        if (tfIdf > 0) {
                            docVec.add(tfItem.getKey(), Math.log(1 + idxInfo.get(tfItem.getKey()).docFreq) * tfIdf);
                        }
                    }
                    break;
            }
            docVec.sort();
            docVec = ModelUtils.cutLowWeights(docVec, cutLowWeightsPerc);
            if (normalizeVectors) {
                ModelUtils.tryNrmVecL2(docVec);
            }
            bows.add(docVec);
        }
        return bows;
    }

    private void processDocumentNGrams(List<WordStem> nGrams, int startIdx, Map<Integer, Integer> tfVec) {
        String nGramStem = "";
        String nGram = "";
        for (int i = startIdx; i < nGrams.size(); i++) {
            nGram += nGrams.get(i).word;
            nGramStem += nGrams.get(i).stem;
            if (wordInfo.containsKey(nGramStem)) {
                int stemIdx = wordInfo.get(nGramStem).idx;
                Integer idx = tfVec.get(stemIdx);
                tfVec.put(stemIdx, idx == null ? 1 : ++idx);
            }
            nGram += " ";
            nGramStem += " ";
        }
    }

    public SparseVector processDocument(String document) {
        return processDocument(document, true);
    }

    public SparseVector processDocument(String document, boolean normalizeTokens) {
        return processDocument(document, stemmer, normalizeTokens);
    }

    public SparseVector processDocument(String document, Stemmer stemmer, boolean normalizeTokens) {
        Preconditions.checkNotNull(document);
        Map<Integer, Integer> tfVec = new HashMap<Integer, Integer>();
        List<WordStem> nGrams = Lists.newArrayListWithCapacity(maxNGramLen);
        for (Token token : tokenizer.getTokens(document)) {
            String word = normalizeText(token.getText(), normalizeTokens);
            if (stopWords == null || !stopWords.isStopWord(word)) {
                String stem = stemmer == null ? word : normalizeText(stemmer.getStem(word), normalizeTokens);
                if (nGrams.size() < maxNGramLen) {
                    WordStem wordStem = new WordStem();
                    wordStem.word = word;
                    wordStem.stem = stem;
                    nGrams.add(wordStem);
                    if (nGrams.size() < maxNGramLen) {
                        continue;
                    }
                } else {
                    WordStem wordStem = nGrams.get(0);
                    wordStem.word = word;
                    wordStem.stem = stem;
                    for (int i = 0; i < maxNGramLen - 1; i++) {
                        nGrams.set(i, nGrams.get(i + 1));
                    }
                    nGrams.set(maxNGramLen - 1, wordStem);
                }
                processDocumentNGrams(nGrams, 0, tfVec);
            }
        }
        int startIdx = nGrams.size() == maxNGramLen ? 1 : 0;
        for (int i = startIdx; i < nGrams.size(); i++) {
            processDocumentNGrams(nGrams, i, tfVec);
        }
        SparseVector docVec = new SparseVector();
        switch (wordWeightType) {
            case TERM_FREQ:
                for (Map.Entry<Integer, Integer> tfItem : tfVec.entrySet()) {
                    docVec.add(tfItem.getKey(), tfItem.getValue());
                }
                break;
            case TF_IDF:
                for (Map.Entry<Integer, Integer> tfItem : tfVec.entrySet()) {
                    double tfIdf = (double) tfItem.getValue() * idxInfo.get(tfItem.getKey()).idf;
                    if (tfIdf > 0) {
                        docVec.add(tfItem.getKey(), tfIdf);
                    }
                }
                break;
            case LOG_DF_TF_IDF:
                for (Map.Entry<Integer, Integer> tfItem : tfVec.entrySet()) {
                    double tfIdf = (double) tfItem.getValue() * idxInfo.get(tfItem.getKey()).idf;
                    if (tfIdf > 0) {
                        docVec.add(tfItem.getKey(), Math.log(1 + idxInfo.get(tfItem.getKey()).docFreq) * tfIdf);
                    }
                }
                break;
        }
        docVec.sort();
        docVec = ModelUtils.cutLowWeights(docVec, cutLowWeightsPerc);
        if (normalizeVectors) {
            ModelUtils.tryNrmVecL2(docVec);
        }
        return docVec;
    }

    public List<Keyword> getKeywords(SparseVector bowVec) {
        Preconditions.checkNotNull(bowVec);
        List<Keyword> keywords = Lists.newArrayListWithCapacity(bowVec.size());
        for (VectorEntry item : bowVec) {
            keywords.add(new Keyword(item.getData(), idxInfo.get(item.getIndex())));
        }
        Collections.sort(keywords, Collections.<Keyword>reverseOrder());
        return keywords;
    }

    public List<Word> getKeywords(SparseVector bowVec, int n) {
        Preconditions.checkNotNull(bowVec);
        Preconditions.checkArgument(n > 0);
        List<Keyword> keywords = getKeywords(bowVec);
        int keywordCount = Math.min(n, keywords.size());
        List<Word> result = Lists.newArrayListWithCapacity(keywordCount);
        for (int i = 0; i < keywordCount; i++) {
            result.add(keywords.get(i).getWord());
        }
        return result;
    }

    public String getKeywordsStr(SparseVector bowVec, int n) {
        List<Word> keywords = getKeywords(bowVec, n);
        if (keywords.size() == 0) {
            return "";
        }
        String keywordsStr = keywords.get(0).mostFrequentForm;
        for (int i = 1; i < keywords.size(); i++) {
            keywordsStr += ", " + keywords.get(i).mostFrequentForm;
        }
        return keywordsStr;
    }

    private static class WordStem
    {
        public String word;
        public String stem;
    }

    public static class Keyword implements Comparable<Keyword>
    {
        private final double factor;
        private final Word word;

        public Keyword(double factor, Word word) {
            this.factor = factor;
            this.word = word;
        }

        public double getFactor() {
            return factor;
        }

        public Word getWord() {
            return word;
        }

        @Override
        public int compareTo(Keyword o) {
            return Double.compare(factor, o.factor);
        }
    }
}
