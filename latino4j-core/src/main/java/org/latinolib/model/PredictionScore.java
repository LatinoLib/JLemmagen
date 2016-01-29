package org.latinolib.model;

/**
 * Author saxo
 */
public class PredictionScore<T> implements Comparable<PredictionScore>
{
    private final double score;
    private final T label;

    public PredictionScore(double score, T label) {
        this.score = score;
        this.label = label;
    }

    public double getScore() {
        return score;
    }

    public T getLabel() {
        return label;
    }

    @Override
    public int compareTo(PredictionScore o) {
        return Double.compare(score, o.score);
    }
}
