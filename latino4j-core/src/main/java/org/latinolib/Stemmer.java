package org.latinolib;

import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.*;

public class Stemmer {
    private Language language;

    public Stemmer(Language language) {
        this.language = language;
    }

    private SnowballStemmer createStemmer() {
        switch (language) {
            case ENGLISH:
                return new englishStemmer();
            case GERMAN:
                return new germanStemmer();
            case FRENCH:
                return new frenchStemmer();
            case SPANISH:
                return new spanishStemmer();
            case ITALIAN:
                return new italianStemmer();
            case PORTUGUESE:
                return new portugueseStemmer();
            case DANISH:
                return new danishStemmer();
            case DUTCH:
                return new dutchStemmer();
            case FINNISH:
                return finnishStemmer.instance; // special case
            case NORWEGIAN:
                return new norwegianStemmer();
            case RUSSIAN:
                return new russianStemmer();
            case SWEDISH:
                return new swedishStemmer();
            default:
                return null;
        }
    }

    public String getStem(String word) {
        try {
            SnowballStemmer stemmer = createStemmer();
            stemmer.setCurrent(word);
            stemmer.stem();
            return stemmer.getCurrent();
        }
        catch (Exception e) { return word; }
    }
}
