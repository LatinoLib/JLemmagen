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

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory; // javadocs
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * Filter factory for {@link LemmagenFilter}.
 *
 * @see <a href="https://bitbucket.org/hlavki/jlemmagen">JLemmaGen web site</a>
 */
public class LemmagenFilterFactory extends TokenFilterFactory {

    /**
     * Lexicon. Default is english
     */
    private String lexiconResource = "mlteast-en";

    /**
     * Schema attribute.
     */
    public static final String LEXICON_SCHEMA_ATTRIBUTE = "lexicon";

    /**
     * Sole constructor. See {@link AbstractAnalysisFactory} for initialization lifecycle.
     *
     * @param args
     */
    public LemmagenFilterFactory(Map<String, String> args) {
        super(args);
        lexiconResource = args.get(LEXICON_SCHEMA_ATTRIBUTE);
    }

    /**
     * {@inheritDoc}
     *
     * @param ts
     * @return
     */
    @Override
    public TokenStream create(TokenStream ts) {
        return new LemmagenFilter(ts, lexiconResource, luceneMatchVersion);
    }
}
