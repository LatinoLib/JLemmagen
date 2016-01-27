package org.latinolib;

import eu.hlavki.text.lemmagen.LemmatizerFactory;
import java.io.IOException;

/**
 * Author mIHA
 */
public class Lemmatizer implements Stemmer {
    private eu.hlavki.text.lemmagen.api.Lemmatizer lemmatizer;

    public Lemmatizer(Language language) throws IOException {
        lemmatizer = createLemmatizer(language);
    }

    private eu.hlavki.text.lemmagen.api.Lemmatizer createLemmatizer(Language language) throws IOException {
        switch (language) {
            case EN:
                return LemmatizerFactory.getPrebuilt("mlteast-en");
            case BG:
                return LemmatizerFactory.getPrebuilt("mlteast-bg");
            case CS:
                return LemmatizerFactory.getPrebuilt("mlteast-cs");
            case ET:
                return LemmatizerFactory.getPrebuilt("mlteast-et");
            case FA:
                return LemmatizerFactory.getPrebuilt("mlteast-fa");
            case FR:
                return LemmatizerFactory.getPrebuilt("mlteast-fr");
            case HU:
                return LemmatizerFactory.getPrebuilt("mlteast-hu");
            case MK:
                return LemmatizerFactory.getPrebuilt("mlteast-mk");
            case PL:
                return LemmatizerFactory.getPrebuilt("mlteast-pl");
            case RO:
                return LemmatizerFactory.getPrebuilt("mlteast-ro");
            case RU:
                return LemmatizerFactory.getPrebuilt("mlteast-ru");
            case SK:
                return LemmatizerFactory.getPrebuilt("mlteast-sk");
            case SL:
                return LemmatizerFactory.getPrebuilt("mlteast-sl");
            case SR:
                return LemmatizerFactory.getPrebuilt("mlteast-sr");
            case UK:
                return LemmatizerFactory.getPrebuilt("mlteast-uk");
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getStem(String word) {
        return lemmatizer.lemmatize(word).toString();
    }
}
