package org.latinolib.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author saxo
 */
public class Prediction<T>
{
    private final List<PredictionScore<T>> scores = Lists.newArrayList();
    private boolean sorted = false;

    public Prediction() {
    }

    public Prediction(PredictionScore<T> score) {
        this.scores.add(Preconditions.checkNotNull(score));
    }

    @SafeVarargs
    public Prediction(PredictionScore<T> score, PredictionScore<T>... scores) {
        Preconditions.checkNotNull(scores);
        this.scores.add(Preconditions.checkNotNull(score));
        if (scores.length > 0) {
            this.scores.addAll(Arrays.asList(scores));
        }
    }

    public void add(double score, T label) {
        Preconditions.checkNotNull(label);
        scores.add(new PredictionScore<T>(score, label));
        sorted = false;
    }

    public PredictionScore<T> getBest() {
        checkSorted();
        return scores.isEmpty() ? null : scores.get(0);
    }

    private void checkSorted() {
        if (!sorted) {
            Collections.sort(scores, Collections.<PredictionScore<T>>reverseOrder());
            sorted = true;
        }
    }
}
