package org.latinolib;

public enum Language {
    UNSPECIFIED,
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
    IS;

    public Stemmer getStemmer() {
        return new SnowballStemmer(this);
    }

    public Stemmer getLemmatizer() {
        return null; // TODO
    }

    public Object getStopWords() {
        return null; // TODO
    }

    public static Language detect(String text) {
        return null; // TODO
    }
}
