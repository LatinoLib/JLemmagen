package org.latinolib.stemmer;

import java.io.Serializable;

/**
 * Author mIHA
 */
public interface Stemmer extends Serializable
{
    String getStem(String word);
}