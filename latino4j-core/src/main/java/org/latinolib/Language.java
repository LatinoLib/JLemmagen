package org.latinolib;

import java.io.IOException;

/**
 * Author mIHA
 */
public enum Language {
    EN,
    FR,
    DE,
    ES,
    DA,
    NL,
    FI,
    IT,
    NO,
    PT,
    SV,
    SL,
    RO,
    HU,
    ET,
    BG,
    CS,
    RU,
    EL,
    LT,
    LV,
    MT,
    PL,
    SK,
    TR,
    VI,
    IS,
    FA,
    MK,
    SR,
    UK;

    public Stemmer getStemmer() {
        return new SnowballStemmer(this);
    }

    public Stemmer getLemmatizer() throws IOException {
        return new Lemmatizer(this);
    }

    public StopWords getStopWords(boolean caseSensitive) throws IOException {
        return new DefaultStopWords(this, caseSensitive);
    }

    public StopWords getStopWords() throws IOException {
        return getStopWords(false);
    }

    public static Language detect(String text) {
        return null; // TODO
    }
}
