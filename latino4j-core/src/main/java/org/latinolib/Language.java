package org.latinolib;

public enum Language {
    UNSPECIFIED,
    ENGLISH,
    FRENCH,
    GERMAN,
    SPANISH,
    DANISH,
    DUTCH,
    FINNISH,
    ITALIAN,
    NORWEGIAN,
    PORTUGUESE,
    SWEDISH,
    SERBIAN,
    SLOVENE,
    ROMANIAN,
    HUNGARIAN,
    ESTONIAN, // *** missing stop words
    BULGARIAN,
    CZECH,
    RUSSIAN,
    // *** language detection only
    GREEK,
    LITHUANIAN,
    LATVIAN,
    MALTESE,
    POLISH,
    SLOVAK,
    TURKISH,
    VIETNAMESE,
    ICELANDIC;

    public Stemmer getStemmer() {
        return null;
    }
    public Stemmer getLemmatizer() {
        return null;
    }
    public Object getStopWords() {
        return null;
    }
    public static Language detect(String text) {
        return null;
    }
}
