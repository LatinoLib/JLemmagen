package org.latinolib;

import org.junit.Test;
import org.latinolib.DetectedLanguage;
import org.latinolib.Language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.latinolib.Language.*;

/**
 * Author mIHA
 */
public class LanguageDetectorTest
{
    @Test
    public void testExamples() throws IOException {
        String text = "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.";
        assertEquals(Language.detect(text), EN);
        assertEquals(Language.detectMulti(text).size(), 1);
        text = "In der Grammatik bezeichnet Flexion (lateinisch flexio „Biegung“)[1] die Änderung der Gestalt eines Wortes (eines Lexems) zum Ausdruck seiner grammatischen Merkmale bzw. der grammatischen Funktion im Satz. Synonym ist der Begriff Beugung (österreichisch auch Biegung).[2][3] Das zugehörige Verb lautet flektieren (lat. flectere „biegen“, „beugen“).[4]";
        assertEquals(Language.detect(text), DE);
        assertEquals(Language.detectMulti(text).size(), 1);
        text = "En morphologie, domaine de la linguistique, on nomme flexion l'ensemble des modifications subies par le signifiant des mots d'une langue flexionnelle (ou agglutinante, dans une moindre mesure) pour dénoter les traits grammaticaux voulus. La flexion ne crée pas de nouveaux mots, au contraire de la dérivation.";
        assertEquals(Language.detect(text), FR);
        assertEquals(Language.detectMulti(text).size(), 1);
        text = "Taivutus tarkoittaa kieliopissa sanan muokkaamista tai merkitsemistä ilmaisemaan kieliopillista tietoa kuten sijaa, sukua, aikamuotoa, lukua ja persoonaa. Kielet, joissa esiintyy paljon taivutusta, luokitellaan synteettisiksi kieliksi.";
        assertEquals(Language.detect(text), FI);
        assertEquals(Language.detectMulti(text).size(), 1);
        text = "Taivutus tarkoittaa kieliopissa sanan muokkaamista tai merkitsemistä ilmaisemaan kieliopillista tietoa kuten sijaa, sukua, aikamuotoa, lukua ja persoonaa. Kielet, joissa esiintyy paljon taivutusta, luokitellaan synteettisiksi kieliksi. En morphologie, domaine de la linguistique, on nomme flexion l'ensemble des modifications subies par le signifiant des mots d'une langue flexionnelle (ou agglutinante, dans une moindre mesure) pour dénoter les traits grammaticaux voulus. La flexion ne crée pas de nouveaux mots, au contraire de la dérivation.";
        List<Language> detectedLanguages = new ArrayList<Language>();
        for (DetectedLanguage dl : Language.detectMulti(text)) { detectedLanguages.add(dl.getLanguage()); }
        assertEquals(detectedLanguages.size(), 2);
        assertTrue(detectedLanguages.containsAll(Arrays.asList(FI, FR)));
    }

    @Test
    public void testMultithreading() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int t = 0; t < 4; t++) {
            futures.add(es.submit(new Callable<Object>()
            {
                @Override
                public Object call() throws IOException {
                    for (int i = 0; i < 500; i++) {
                        String text = "In grammar, inflection or inflexion is the modification of a word to express different grammatical categories such as tense, case, voice, aspect, person, number, gender and on rare occasion the mood. The inflection of verbs is also called conjugation, and the inflection of nouns, adjectives and pronouns is also called declension.";
                        assertEquals(Language.detect(text), EN);
                        text = "In der Grammatik bezeichnet Flexion (lateinisch flexio „Biegung“)[1] die Änderung der Gestalt eines Wortes (eines Lexems) zum Ausdruck seiner grammatischen Merkmale bzw. der grammatischen Funktion im Satz. Synonym ist der Begriff Beugung (österreichisch auch Biegung).[2][3] Das zugehörige Verb lautet flektieren (lat. flectere „biegen“, „beugen“).[4]";
                        assertEquals(Language.detect(text), DE);
                    }
                    return null;
                }
            }));
        }
        for (Future<?> f : futures) { f.get(); }
    }
}
