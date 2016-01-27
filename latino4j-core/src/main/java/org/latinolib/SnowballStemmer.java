package org.latinolib;

import org.tartarus.snowball.ext.*;

/**
 * Author mIHA
 */
public class SnowballStemmer implements Stemmer {
    private Language language;

    public SnowballStemmer(Language language) {
        this.language = language;
        createStemmer(); // throws exception if language not supported
    }

    private org.tartarus.snowball.SnowballStemmer createStemmer() {
        switch (language) {
            case EN:
                return new englishStemmer();
            case DE:
                return new germanStemmer();
            case FR:
                return new frenchStemmer();
            case ES:
                return new spanishStemmer();
            case IT:
                return new italianStemmer();
            case PT:
                return new portugueseStemmer();
            case DA:
                return new danishStemmer();
            case NL:
                return new dutchStemmer();
            case FI:
                return finnishStemmer.instance; // special case
            case NO:
                return new norwegianStemmer();
            case RU:
                return new russianStemmer();
            case SV:
                return new swedishStemmer();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getStem(String word) {
        org.tartarus.snowball.SnowballStemmer stemmer = createStemmer();
        try {
            stemmer.setCurrent(word);
            stemmer.stem();
            return stemmer.getCurrent();
        } catch (Exception e) { return word; }
    }
}
