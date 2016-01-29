package org.latinolib;

import com.google.common.base.Preconditions;

/**
 * Author mIHA
 */
public class DetectedLanguage implements Comparable<DetectedLanguage>
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

    @Override
    public int compareTo(DetectedLanguage o) {
        return Double.compare(score, Preconditions.checkNotNull(o.score));
    }
}