package org.latinolib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public enum Language
{
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
    PL,
    SK,
    TR,
    IS,
    FA,
    MK,
    SR,
    UK,
    EE,
    TH;

    public static class CC
    {

    }

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

    public static List<DetectedLanguage> detectMulti(String text) throws IOException {
        synchronized (Language.class) { // TODO: thread-safe ranker
            if (languageDetector == null) {
                languageDetector = new NGramProfiles().getRanker();
            }
            languageDetector.reset();
            languageDetector.account(text);
            RankResult rr = languageDetector.getRankResult();
            List<DetectedLanguage> list = new ArrayList<DetectedLanguage>();
            for (int i = 0; i < rr.getLength(); i++) {
                if (rr.getScore(i) > 0.0) {
                    list.add(new DetectedLanguage(rr.getScore(i), Language.valueOf(rr.getName(i).toUpperCase())));
                }
            }
            Collections.sort(list, Collections.<DetectedLanguage>reverseOrder());
            return list;
        }
    }

    public static Language detect(String text) throws IOException {
        List<DetectedLanguage> list = detectMulti(text);
        return list.size() > 0 ? list.get(0).getLanguage() : null;
    }
}
