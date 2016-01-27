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
        return LemmatizerFactory.getPrebuilt("mlteast-" + language.toString().toLowerCase());
    }

    @Override
    public String getStem(String word) {
        return lemmatizer.lemmatize(word).toString();
    }
}
