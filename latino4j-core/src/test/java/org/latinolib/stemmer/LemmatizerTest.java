package org.latinolib.stemmer;

import org.junit.Test;
import org.latinolib.Language;
import org.latinolib.tokenizer.SimpleTokenizer;
import org.latinolib.tokenizer.SimpleTokenizerType;
import org.latinolib.tokenizer.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.latinolib.Language.*;

/**
 * Author mIHA
 */
public class LemmatizerTest
{
    @Test
    public void testEmpty() throws IOException {
        for (Language lang : Language.values()) {
            try {
                assertEquals(lang.getLemmatizer().getStem(""), "");
            } catch (IOException e) { }
        }
    }

    @Test
    public void testExamples() throws IOException {
        Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        String[] stems = new String[] {
            "In", "grammar", "inflection", "or", "inflexion", "be", "the", "modification", "of", "a", "word", "to", "express", "different", "grammatical", "category", "such", "as", "tense", "case", "voice", "aspect", "person", "number", "gender", "and", "on", "rare", "occasion", "the", "mood", "The", "inflection", "of", "verb", "be", "also", "call", "conjugation", "and", "the", "inflection", "of", "noun", "adjective", "and", "pronoun", "be", "also", "call", "declension"};
        int i = 0;
        for (Token token : tokens) {
            String stem = EN.getLemmatizer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
        tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "En morphologie, domaine de la linguistique, on nomme flexion l'ensemble des modifications subies par le signifiant des mots d'une langue flexionnelle (ou agglutinante, dans une moindre mesure) pour dénoter les traits grammaticaux voulus. La flexion ne crée pas de nouveaux mots, au contraire de la dérivation.");
        stems = new String[] {
            "En", "morphologie", "domaine", "de", "le", "linguistique", "on", "nommer", "flexion", "l", "ensemble", "un", "modification", "subir", "par", "le", "signifier", "un", "mot", "d", "un", "langue", "flexionnel", "ou", "agglutinant", "dans", "un", "moindre", "mesurer", "pour", "dénoter", "le", "trait", "grammatical", "vouloir", "L", "flexion", "ne", "créer", "pas", "de", "nouveau", "mot", "à+le", "contraire", "de", "le", "dérivation"};
            i = 0;
        for (Token token : tokens) {
            String stem = FR.getLemmatizer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
        tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "Словозміна (також флексія), системне творення різних форм того самого слова відповідно до його синтаксичних пов'язань з ін.");
        stems = new String[] {
            "Словозміна", "також", "флексія", "системний", "творення", "різний", "форма", "той", "самий", "слово", "відповідно", "до", "він", "синтаксичний", "пов", "язання", "з", "ін"};
        i = 0;
        for (Token token : tokens) {
            String stem = UK.getLemmatizer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
    }

    @Test
    public void testMultithreadingEn() throws InterruptedException, IOException, ExecutionException {
        final Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        final String[] stems = new String[] {
            "In", "grammar", "inflection", "or", "inflexion", "be", "the", "modification", "of", "a", "word", "to", "express", "different", "grammatical", "category", "such", "as", "tense", "case", "voice", "aspect", "person", "number", "gender", "and", "on", "rare", "occasion", "the", "mood", "The", "inflection", "of", "verb", "be", "also", "call", "conjugation", "and", "the", "inflection", "of", "noun", "adjective", "and", "pronoun", "be", "also", "call", "declension"};
        final Stemmer stemmer = EN.getLemmatizer();
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
                            String stem = stemmer.getStem(token.getText());
                            assertEquals(stems[j++], stem);
                        }
                    }
                    return null;
                }
            }));
        }
        for (Future<?> f : futures) { f.get(); }
    }

    @Test
    public void testMultithreadingUk() throws InterruptedException, IOException, ExecutionException {
        final Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "Словозміна (також флексія), системне творення різних форм того самого слова відповідно до його синтаксичних пов'язань з ін.");
        final String[] stems = new String[] {
            "Словозміна", "також", "флексія", "системний", "творення", "різний", "форма", "той", "самий", "слово", "відповідно", "до", "він", "синтаксичний", "пов", "язання", "з", "ін"};
        final Stemmer stemmer = UK.getLemmatizer();
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
                            String stem = stemmer.getStem(token.getText());
                            assertEquals(stems[j++], stem);
                        }
                    }
                    return null;
                }
            }));
        }
        for (Future<?> f : futures) { f.get(); }
    }

    @Test
    public void testMultithreadingConstructor() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int t = 0; t < 4; t++) {
            futures.add(es.submit(new Callable<Object>()
            {
                @Override
                public Object call() throws IOException {
                    for (int i = 0; i < 50; i++) {
                        EN.getLemmatizer();
                    }
                    return null;
                }
            }));
        }
        for (Future<?> f : futures) { f.get(); }
    }
}
