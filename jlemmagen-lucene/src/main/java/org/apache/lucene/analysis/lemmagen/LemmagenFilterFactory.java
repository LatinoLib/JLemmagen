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
