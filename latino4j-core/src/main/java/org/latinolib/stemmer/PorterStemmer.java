package org.latinolib.stemmer;

import org.tartarus.snowball.ext.porterStemmerOriginal;

/**
 * Author mIHA
 */
public class PorterStemmer implements Stemmer
{
    @Override
    public String getStem(String word) {
        return porterStemmerOriginal.getStem(word);
    }
}
