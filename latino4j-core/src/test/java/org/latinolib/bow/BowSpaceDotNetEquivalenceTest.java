package org.latinolib.bow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.latinolib.Language;
import org.latinolib.SparseVector;
import org.latinolib.VectorEntry;
import org.latinolib.stemmer.Lemmatizer;
import org.latinolib.tokenizer.SimpleTokenizer;
import org.latinolib.tokenizer.SimpleTokenizerType;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

/**
 * Author saxo
 *
 * Note: this is actually equivalence test between the Java and LATINO.net implementation.
 */
public class BowSpaceDotNetEquivalenceTest
{
    public static final String[] CORPUS = "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?. On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.".split("\\.");
    public static final String DOCUMENT = "embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful";
    public static final Map<String, DotNetIdxDat[]> DOT_NET_RESULTS;

    static {
        InputStream stream = BowSpaceDotNetEquivalenceTest.class.getResourceAsStream("dotnet_test_output.json");
        Type type = new TypeToken<Map<String, DotNetIdxDat[]>>(){}.getType();
        DOT_NET_RESULTS = new Gson().fromJson(new InputStreamReader(stream), type);
    }

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testWordWeightType_TERM_FREQ() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testWordWeightType_TF_IDF() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TF_IDF);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testWordWeightType_LOG_DF_TF_IDF() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.LOG_DF_TF_IDF);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testNGramLen() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(5);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testMinWordFreq() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(3);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testNormalizeVectors() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(false);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testKeepWordForms() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(true);

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testLemmatizer() throws IOException {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);
        bow.setStemmer(new Lemmatizer(Language.EN));

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testStopWords() throws IOException {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);
        bow.setStemmer(new Lemmatizer(Language.EN));
        bow.setStopWords(Language.EN.getStopWords());

        bow.initialize(Arrays.asList(CORPUS), true, false);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testInitializeLargeScale() {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setStopWords(null);
        bow.setStemmer(null);
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);

        bow.initialize(Arrays.asList(CORPUS), true, true);

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get(testName.getMethodName());
        assertArrayEquals(expected, vector.toArray());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(new SimpleTokenizer(SimpleTokenizerType.ALL_CHARS, 2));
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(1);
        bow.setWordWeightType(WordWeightType.TERM_FREQ);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);
        bow.setLemmatizerByLanguage(Language.EN);
        bow.setStopWords(Language.EN.getStopWords());

        bow.initialize(Arrays.asList(CORPUS), true, false);

        // serialize + deserialize
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(bow);
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream oin = new ObjectInputStream(bin);
        bow = (BowSpace) oin.readObject();

        SparseVector vector = bow.processDocument(DOCUMENT, true);
        DotNetIdxDat[] expected = DOT_NET_RESULTS.get("testStopWords");
        assertArrayEquals(expected, vector.toArray());
    }

    private static class DotNetIdxDat
    {
        public int Idx;
        public double Dat;

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (this == o) return true;
            if (getClass() != o.getClass()) {
                if (VectorEntry.class.isAssignableFrom(o.getClass())) {
                    int idx = ((VectorEntry)o).getIndex();
                    double dat = ((VectorEntry)o).getData();
                    o = new DotNetIdxDat();
                    ((DotNetIdxDat)o).Idx = idx;
                    ((DotNetIdxDat)o).Dat = dat;
                }
            }
            if (getClass() != o.getClass()) return false;

            DotNetIdxDat that = (DotNetIdxDat) o;
            if (Idx != that.Idx) return false;
            return Dat == that.Dat;
        }
    }
}