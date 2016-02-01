package org.latinolib;

import com.google.common.base.Preconditions;

/**
 * Author mIHA
 */
public class DetectedLanguage
{
    private final double score;
    private final Language language;

    public DetectedLanguage(double score, Language language) {
        this.score = score;
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public double getScore() {
        return score;
    }
}