package org.latinolib;

import java.io.IOException;
import de.spieleck.app.cngram.NGramProfiles;
import de.spieleck.app.cngram.NGramProfiles.*;
import org.latinolib.stemmer.Lemmatizer;
import org.latinolib.stemmer.SnowballStemmer;
import org.latinolib.stemmer.Stemmer;
import org.latinolib.stopwords.DefaultStopWords;
import org.latinolib.stopwords.StopWords;

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

    private static Ranker languageDetector
        = null;

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

    // TODO: public static KeyDat<double, Language>[] detectMulti

    public static Language detect(String text) throws IOException {
        synchronized (Language.class) { // TODO: thread-safe ranker
            if (languageDetector == null) {
                languageDetector = new NGramProfiles().getRanker();
            }
            languageDetector.reset();
            languageDetector.account(text);
            RankResult rr = languageDetector.getRankResult();
            if (rr.getLength() > 0 && rr.getScore(0) > 0.0) {
                return Language.valueOf(rr.getName(0).toUpperCase()); // TODO: check if we support all languages
            }
            return null;
        }
    }
}
