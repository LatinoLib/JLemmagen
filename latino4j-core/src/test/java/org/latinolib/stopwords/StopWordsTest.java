package org.latinolib.stopwords;

import org.junit.Test;
import org.latinolib.Language;
import org.latinolib.tokenizer.SimpleTokenizer;
import org.latinolib.tokenizer.SimpleTokenizerType;
import org.latinolib.tokenizer.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.latinolib.Language.*;

/**
 * Author mIHA
 */
public class StopWordsTest
{
    @Test
    public void testEmpty() {
        for (Language lang : Language.values()) {
            try {
                assertEquals(lang.getStopWords().isStopWord(""), false);
            } catch (IOException e) { }
        }
    }

    @Test
    public void testExamples() throws IOException {
        Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        String[] stopWordsTruth = new String[] {
            "In", "or", "is", "the", "of", "a", "to", "such", "as", "and", "on", "the", "The", "of", "is", "and", "the", "of", "and", "is"};
        String[] stopWordsCaseSensitiveTruth = new String[] {
            "or", "is", "the", "of", "a", "to", "such", "as", "and", "on", "the", "of", "is", "and", "the", "of", "and", "is"};
        StopWords stopWords = EN.getStopWords();
        StopWords stopWordsCaseSensitive = EN.getStopWords(true);
        int i = 0, j = 0;
        for (Token token : tokens) {
            if (stopWords.isStopWord(token.getText())) {
                assertEquals(stopWordsTruth[i++], token.getText());
            }
            if (stopWordsCaseSensitive.isStopWord(token.getText())) {
                assertEquals(stopWordsCaseSensitiveTruth[j++], token.getText());
            }
        }
        tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In der Grammatik bezeichnet Flexion (lateinisch flexio „Biegung“)[1] die Änderung der Gestalt eines Wortes (eines Lexems) zum Ausdruck seiner grammatischen Merkmale bzw. der grammatischen Funktion im Satz. Synonym ist der Begriff Beugung (österreichisch auch Biegung).[2][3] Das zugehörige Verb lautet flektieren (lat. flectere „biegen“, „beugen“).[4]");
        stopWordsTruth = new String[] {
            "In", "der", "die", "der", "eines", "eines", "zum", "seiner", "der", "im", "ist", "der", "auch", "Das"};
        stopWordsCaseSensitiveTruth = new String[] {
            "der", "die", "der", "eines", "eines", "zum", "seiner", "der", "im", "ist", "der", "auch"};
        stopWords = DE.getStopWords();
        stopWordsCaseSensitive = DE.getStopWords(true);
        i = j = 0;
        for (Token token : tokens) {
            if (stopWords.isStopWord(token.getText())) {
                assertEquals(stopWordsTruth[i++], token.getText());
            }
            if (stopWordsCaseSensitive.isStopWord(token.getText())) {
                assertEquals(stopWordsCaseSensitiveTruth[j++], token.getText());
            }
        }
    }

    @Test
    public void testMultithreading() throws IOException, ExecutionException, InterruptedException {
        final Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        final String[] stopWordsTruth = new String[] {
            "In", "or", "is", "the", "of", "a", "to", "such", "as", "and", "on", "the", "The", "of", "is", "and", "the", "of", "and", "is"};
        final StopWords stopWords = EN.getStopWords();
        ExecutorService es = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int t = 0; t < 4; t++) {
            futures.add(es.submit(new Callable<Object>()
            {
                @Override
                public Object call() {
                    for (int i = 0; i < 50000; i++) {
                        int j = 0;
                        for (Token token : tokens) {
                            if (stopWords.isStopWord(token.getText())) {
                                assertEquals(stopWordsTruth[j++], token.getText());
                            }
                        }
                    }
                    return null;
                }
            }));
        }
        for (Future<?> f : futures) { f.get(); }
    }

    @Test
    public void testMultithreadingConstructor() throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int t = 0; t < 4; t++) {
            futures.add(es.submit(new Callable<Object>()
            {
                @Override
                public Object call() throws IOException {
                    for (int i = 0; i < 1000; i++) {
                        EN.getStopWords();
                    }
                    return null;
                }
            }));
        }
        for (Future<?> f : futures) { f.get(); }
    }
}
