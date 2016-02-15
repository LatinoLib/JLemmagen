package org.latinolib.stemmer;

import org.tartarus.snowball.ext.porterStemmerOriginal;

import java.io.Serializable;

/**
 * Author mIHA
 */
public class PorterStemmer implements Stemmer
{
    private static final long serialVersionUID = -8630180403973087053L;

    @Override
    public String getStem(String word) {
        return porterStemmerOriginal.getStem(word);
    }
}
