package org.latinolib;

import org.tartarus.snowball.ext.porterStemmerOriginal;

public class PorterStemmer implements IStemmer {
    public String getStem(String word) {
        return porterStemmerOriginal.getStem(word);
    }
}
