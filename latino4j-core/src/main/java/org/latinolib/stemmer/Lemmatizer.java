package org.latinolib.stemmer;

import eu.hlavki.text.lemmagen.LemmatizerFactory;
import org.latinolib.Language;
import org.latinolib.stemmer.Stemmer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Author mIHA
 */
public class Lemmatizer implements Stemmer, Serializable
{
    private static final long serialVersionUID = 4682371004630411041L;

    private transient eu.hlavki.text.lemmagen.api.Lemmatizer lemmatizer;
    private final Language language;

    public Lemmatizer(Language language) throws IOException {
        this.language = language;
        lemmatizer = createLemmatizer(language);
    }

    private eu.hlavki.text.lemmagen.api.Lemmatizer createLemmatizer(Language language) throws IOException {
        return LemmatizerFactory.getPrebuilt("mlteast-" + language.toString().toLowerCase());
    }

    @Override
    public String getStem(String word) {
        return lemmatizer.lemmatize(word).toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        lemmatizer = createLemmatizer(language);
    }
}
