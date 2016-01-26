package org.latinolib;

import eu.hlavki.text.lemmagen.LemmatizerFactory;
import java.io.IOException;

public class Lemmatizer implements Stemmer {
    private eu.hlavki.text.lemmagen.api.Lemmatizer lemmatizer;

    public Lemmatizer(Language language) throws IOException {
        lemmatizer = createLemmatizer(language);
    }

    private eu.hlavki.text.lemmagen.api.Lemmatizer createLemmatizer(Language language) throws IOException {
        switch (language) {
            case EN:
                return LemmatizerFactory.getPrebuilt("mlteast-en");
//            case DE:
//                return new germanStemmer();
//            case FR:
//                return new frenchStemmer();
//            case ES:
//                return new spanishStemmer();
//            case IT:
//                return new italianStemmer();
//            case PT:
//                return new portugueseStemmer();
//            case DA:
//                return new danishStemmer();
//            case NL:
//                return new dutchStemmer();
//            case FI:
//                return finnishStemmer.instance; // special case
//            case NO:
//                return new norwegianStemmer();
//            case RU:
//                return new russianStemmer();
//            case SV:
//                return new swedishStemmer();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getStem(String word) {
        return lemmatizer.lemmatize(word).toString();
    }
}
