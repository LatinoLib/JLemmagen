package org.latinolib.stemmer;

import org.junit.Test;
import org.latinolib.Language;
import org.latinolib.tokenizer.SimpleTokenizer;
import org.latinolib.tokenizer.SimpleTokenizerType;
import org.latinolib.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.latinolib.Language.*;

/**
 * Author mIHA
 */
public class StemmerTest
{
    @Test
    public void testEmpty() {
        for (Language lang : Language.values()) {
            try {
                assertEquals(lang.getStemmer().getStem(""), "");
            } catch (UnsupportedOperationException e) { }
        }
    }

    @Test
    public void testExamples() {
        Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        String[] stems = new String[] {
            "In", "grammar", "inflect", "or", "inflexion", "is", "the", "modif", "of", "a", "word", "to", "express", "differ", "grammat", "categori", "such", "as", "tens", "case", "voic", "aspect", "person", "number", "gender", "and", "on", "rare", "occas", "the", "mood", "The", "inflect", "of", "verb", "is", "also", "call", "conjug", "and", "the", "inflect", "of", "noun", "adject", "and", "pronoun", "is", "also", "call", "declens"};
        int i = 0;
        for (Token token : tokens) {
            String stem = EN.getStemmer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
        tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In der Grammatik bezeichnet Flexion (lateinisch flexio „Biegung“)[1] die Änderung der Gestalt eines Wortes (eines Lexems) zum Ausdruck seiner grammatischen Merkmale bzw. der grammatischen Funktion im Satz. Synonym ist der Begriff Beugung (österreichisch auch Biegung).[2][3] Das zugehörige Verb lautet flektieren (lat. flectere „biegen“, „beugen“).[4]");
        stems = new String[] {
            "In", "der", "Grammat", "bezeichnet", "Flexion", "latein", "flexio", "Biegung", "die", "Änderung", "der", "Gestalt", "ein", "Wort", "ein", "Lexem", "zum", "Ausdruck", "sein", "grammat", "Merkmal", "bzw", "der", "grammat", "Funktion", "im", "Satz", "Synonym", "ist", "der", "Begriff", "Beugung", "osterreich", "auch", "Biegung", "Das", "zugehor", "Verb", "lautet", "flekti", "lat", "flect", "bieg", "beug"};
        i = 0;
        for (Token token : tokens) {
            String stem = DE.getStemmer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
        tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "En morphologie, domaine de la linguistique, on nomme flexion l'ensemble des modifications subies par le signifiant des mots d'une langue flexionnelle (ou agglutinante, dans une moindre mesure) pour dénoter les traits grammaticaux voulus. La flexion ne crée pas de nouveaux mots, au contraire de la dérivation.");
        stems = new String[] {
            "En", "morpholog", "domain", "de", "la", "linguist", "on", "nomm", "flexion", "l", "ensembl", "de", "modif", "sub", "par", "le", "signifi", "de", "mot", "d", "une", "langu", "flexionnel", "ou", "agglutin", "dan", "une", "moindr", "mesur", "pour", "dénot", "le", "trait", "grammatical", "voulus", "La", "flexion", "ne", "cré", "pas", "de", "nouveau", "mot", "au", "contrair", "de", "la", "dériv"};
        i = 0;
        for (Token token : tokens) {
            String stem = FR.getStemmer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
        tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "Taivutus tarkoittaa kieliopissa sanan muokkaamista tai merkitsemistä ilmaisemaan kieliopillista tietoa kuten sijaa, sukua, aikamuotoa, lukua ja persoonaa. Kielet, joissa esiintyy paljon taivutusta, luokitellaan synteettisiksi kieliksi.");
        stems = new String[] {
            "Taivutus", "tarkoit", "kieliop", "sana", "muokkaam", "tai", "merkitsem", "ilmaisem", "kieliopil", "tieto", "kute", "sija", "suku", "aikamuoto", "luku", "ja", "persoon", "Kiele", "jois", "esiintyy", "palj", "taivutu", "luokit", "synteettis", "kiel"};
        i = 0;
        for (Token token : tokens) {
            String stem = FI.getStemmer().getStem(token.getText());
            assertEquals(stems[i++], stem);
        }
    }

    @Test
    public void testMultithreadingEn() throws InterruptedException, ExecutionException {
        final Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        final String[] stems = new String[] {
            "In", "grammar", "inflect", "or", "inflexion", "is", "the", "modif", "of", "a", "word", "to", "express", "differ", "grammat", "categori", "such", "as", "tens", "case", "voic", "aspect", "person", "number", "gender", "and", "on", "rare", "occas", "the", "mood", "The", "inflect", "of", "verb", "is", "also", "call", "conjug", "and", "the", "inflect", "of", "noun", "adject", "and", "pronoun", "is", "also", "call", "declens"};
        final Stemmer stemmer = EN.getStemmer();
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
    public void testMultithreadingFi() throws InterruptedException, ExecutionException {
        final Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "Taivutus tarkoittaa kieliopissa sanan muokkaamista tai merkitsemistä ilmaisemaan kieliopillista tietoa kuten sijaa, sukua, aikamuotoa, lukua ja persoonaa. Kielet, joissa esiintyy paljon taivutusta, luokitellaan synteettisiksi kieliksi.");
        final String[] stems = new String[] {
            "Taivutus", "tarkoit", "kieliop", "sana", "muokkaam", "tai", "merkitsem", "ilmaisem", "kieliopil", "tieto", "kute", "sija", "suku", "aikamuoto", "luku", "ja", "persoon", "Kiele", "jois", "esiintyy", "palj", "taivutu", "luokit", "synteettis", "kiel"};
        final Stemmer stemmer = FI.getStemmer();
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
    public void testMultithreadingPorter() throws InterruptedException, ExecutionException {
        final Iterable<Token> tokens = new SimpleTokenizer(SimpleTokenizerType.ALPHA_ONLY).getTokens(
            "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.");
        final String[] stems = new String[]{
            "In", "grammar", "inflect", "or", "inflexion", "is", "the", "modif", "of", "a", "word", "to", "express", "differ", "grammat", "categori", "such", "as", "tens", "case", "voic", "aspect", "person", "number", "gender", "and", "on", "rare", "occas", "the", "mood", "The", "inflect", "of", "verb", "is", "also", "call", "conjug", "and", "the", "inflect", "of", "noun", "adject", "and", "pronoun", "is", "also", "call", "declens"};
        final Stemmer stemmer = new PorterStemmer();
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
}
