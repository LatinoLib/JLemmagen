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
