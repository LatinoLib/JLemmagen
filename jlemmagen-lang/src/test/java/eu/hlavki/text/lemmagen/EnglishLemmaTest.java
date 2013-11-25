/*
 * Copyright 2013 Michal Hlavac <hlavki@hlavki.eu>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hlavki.text.lemmagen;

import eu.hlavki.text.lemmagen.api.Lemmatizer;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michal Hlavac <hlavki@hlavki.eu>
 */
public class EnglishLemmaTest {

    public EnglishLemmaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void longEnglishText() {
        try {
            Lemmatizer lm = LemmatizerFactory.getPrebuild("mlteast-en");
            String text = "On the other hand, inflectional paradigms, "
                    + "or lists of inflected forms of typical words (such as sing, sang, "
                    + "sung, sings, singing, singer, singers, song, songs, songstress, "
                    + "songstresses in English) need to be analyzed according to criteria "
                    + "for uncovering the underlying lexical stem.";
            String[] words = text.split("(?=[,.])|\\s+");
            for (String word : words) {
                if (word.trim().length() > 1) {
                    CharSequence lemma = lm.lemmatize(word.trim());
                    if (!word.equals(lemma)) {
                        System.out.println(word + " -> " + lemma);
                    }
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shortEnglishText() {
        try {
            Lemmatizer lm = LemmatizerFactory.getPrebuild("mlteast-en");
            assertEquals("be", lm.lemmatize("are"));
            assertEquals("sing", lm.lemmatize("singing"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testMakedoniaWord() {
        try {
            Lemmatizer lm = LemmatizerFactory.getPrebuild("mlteast-mk");
            assertEquals("инвестиција", lm.lemmatize("инвестиции"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
