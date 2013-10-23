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
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 *
 * @author Michal Hlavac <hlavki@hlavki.eu>
 */
public class LemmagenFilterTest extends BaseTokenStreamTestCase {

    private static final String[] ACTUAL_WORDS = new String[]{"respond", "are", "uninflected", "items", "underlying", "singing"};
    private static final String[] LEMMA_WORDS = new String[]{"respond", "be", "uninflect", "item", "underlie", "sing"};

    @Test
    public void doFilter() throws IOException {
        Analyzer analyzer = getAnalyzer();
        for (int idx = 0; idx < ACTUAL_WORDS.length; idx++) {
            assertAnalyzesTo(analyzer, ACTUAL_WORDS[idx], new String[] {LEMMA_WORDS[idx]});
        }
    }

    private Analyzer getAnalyzer() {
        return new Analyzer() {

            @Override
            protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
                StandardTokenizer source = new StandardTokenizer(Version.LUCENE_45, reader);
                LemmagenFilter filter = new LemmagenFilter(source, "mlteast-en", Version.LUCENE_45);
                return new TokenStreamComponents(source, filter);
            }
        };
    }

}
