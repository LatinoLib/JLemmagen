package org.latinolib.stemmer;

import org.latinolib.Language;
import org.tartarus.snowball.ext.*;

import java.io.Serializable;

/**
 * Author mIHA
 */
public class DefaultStemmer implements Stemmer
{
    private static final long serialVersionUID = 1281198274687573313L;

    private Language language;

    public DefaultStemmer(Language language) {
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
                return new finnishStemmer();
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
