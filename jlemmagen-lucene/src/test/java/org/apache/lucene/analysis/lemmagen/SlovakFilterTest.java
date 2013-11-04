/*
 * Copyright (C) 2013 Michal Hlavac <hlavki@hlavki.eu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apache.lucene.analysis.lemmagen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import static org.apache.lucene.util.LuceneTestCase.TEST_VERSION_CURRENT;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 *
 * @author Michal Hlavac <hlavki@hlavki.eu>
 */
public class SlovakFilterTest extends BaseTokenStreamTestCase {

    @Test
    public void doFilter() throws IOException {
        assertTrue(analyze(getAnalyzer(), "/adam-sangala.txt") < 8000);
    }

    private Analyzer getAnalyzer() {
        return new Analyzer() {

            @Override
            protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
                StandardTokenizer source = new StandardTokenizer(Version.LUCENE_45, reader);

                TokenStream filter = new ASCIIFoldingFilter(new LemmagenFilter(
                        new LowerCaseFilter(TEST_VERSION_CURRENT,
                                new StandardFilter(TEST_VERSION_CURRENT, source)), "mlteast-sk", TEST_VERSION_CURRENT));
                return new Analyzer.TokenStreamComponents(source, filter);
            }
        };
    }

    private int analyze(Analyzer analyzer, String resource) {
        TokenStream stream = null;
        InputStream in = null;
        Set<Integer> words = new HashSet<Integer>();
        try {
            long start = System.currentTimeMillis();
            in = SlovakFilterTest.class.getResourceAsStream(resource);
            stream = analyzer.tokenStream(null, new InputStreamReader(in));
            stream.reset();
            int count = 0;
            while (stream.incrementToken()) {
                String word = stream.getAttribute(CharTermAttribute.class).toString();
                words.add(word.hashCode());
                count++;
            }
            stream.end();
            long end = System.currentTimeMillis();
            System.out.println("All words count: " + count);
            System.out.println("Words set count: " + words.size());
            System.out.println("Commpression: " + ((double) words.size() / count));
            System.out.println("TIME: " + (end - start) + " ms");
            return words.size();
        } catch (IOException e) {
            // not thrown b/c we're using a string reader...
            throw new RuntimeException(e);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (stream != null) try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
