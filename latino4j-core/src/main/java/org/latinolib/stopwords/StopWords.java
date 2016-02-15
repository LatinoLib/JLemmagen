package org.latinolib.stopwords;

import java.io.Serializable;

/**
 * Author mIHA
 */
public interface StopWords extends Serializable
{
    boolean isStopWord(String word);
}
