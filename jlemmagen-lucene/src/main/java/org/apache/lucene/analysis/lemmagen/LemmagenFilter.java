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

import eu.hlavki.text.lemmagen.LemmatizerFactory;
import eu.hlavki.text.lemmagen.api.Lemmatizer;
import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michal Hlavac <hlavki@hlavki.eu>
 */
public class LemmagenFilter extends TokenFilter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LemmagenFilter.class);
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final CharacterUtils charUtils;
    private Lemmatizer lm = null;

    public LemmagenFilter(final TokenStream input, final String lexiconResource, final Version version) {
        super(input);
        this.charUtils = CharacterUtils.getInstance(version);
        try {
            this.lm = LemmatizerFactory.getPrebuild(lexiconResource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't initialize lemmatizer from resource " + lexiconResource, e);
        }
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        charUtils.toLowerCase(termAtt.buffer(), 0, termAtt.length());
        CharSequence lemma = lm.lemmatize(termAtt);
        if (!equalCharSequences(lemma, termAtt)) {
            termAtt.setEmpty().append(lemma);
        }
        return true;
    }

    /**
     * Compare two char sequences for equality. Assumes non-null arguments.
     */
    private boolean equalCharSequences(CharSequence s1, CharSequence s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        if (len1 != len2) return false;
        for (int i = len1; --i >= 0;) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
