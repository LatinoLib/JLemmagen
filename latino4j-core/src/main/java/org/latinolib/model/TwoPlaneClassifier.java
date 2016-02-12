package org.latinolib.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.latinolib.SparseVector;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Author saxo
 */
public class TwoPlaneClassifier<T extends Serializable> implements Model<T, SparseVector>, Serializable
{
    private static final long serialVersionUID = 7509696890306666596L;

    private LinearModel posClassifier;
    private LinearModel negClassifier;

    private transient List<Double> posSortedScores;
    private transient List<Double> negSortedScores;
    private List<ExampleScore> exampleScores;

    private Double biasToPosRate;
    private Double biasToNegRate;
    private boolean scorePercentile;

    private final T negativeLabel;
    private final T neutralLabel;
    private final T positiveLabel;

    public TwoPlaneClassifier(T negativeLabel, T neutralLabel, T positiveLabel) {
        this.negativeLabel = Preconditions.checkNotNull(negativeLabel);
        this.neutralLabel = Preconditions.checkNotNull(neutralLabel);
        this.positiveLabel = Preconditions.checkNotNull(positiveLabel);
        Preconditions.checkArgument(!negativeLabel.equals(neutralLabel) && !negativeLabel.equals(positiveLabel));
        Preconditions.checkArgument(!neutralLabel.equals(positiveLabel));
    }

    public Double getBiasToPosRate() {
        return biasToPosRate;
    }

    public void setBiasToPosRate(Double biasToPosRate) {
        this.biasToPosRate = biasToPosRate;
    }

    public Double getBiasToNegRate() {
        return biasToNegRate;
    }

    public void setBiasToNegRate(Double biasToNegRate) {
        this.biasToNegRate = biasToNegRate;
    }

    public boolean isScorePercentile() {
        return scorePercentile;
    }

    public void setScorePercentile(boolean scorePercentile) {
        this.scorePercentile = scorePercentile;
    }

    public T getNegativeLabel() {
        return negativeLabel;
    }

    public T getNeutralLabel() {
        return neutralLabel;
    }

    public T getPositiveLabel() {
        return positiveLabel;
    }

    @Override
    public void train(LabeledExampleCollection<T, SparseVector> dataset) {
        Preconditions.checkNotNull(dataset);

        LabeledDataset<Double, SparseVector> trainDataset = new LabeledDataset<Double, SparseVector>();
        for (LabeledExampleEntry<T, SparseVector> le : dataset) {
            trainDataset.add(new LabeledExample<Double, SparseVector>(
                positiveLabel.equals(le.getLabel()) ? 1d : (neutralLabel.equals(le.getLabel()) ? 0 : -1d),
                le.getExample()));
        }

        LabeledDataset<Double, SparseVector> posDataset = new LabeledDataset<Double, SparseVector>();
        for (LabeledExampleEntry<Double, SparseVector> le : trainDataset) {
            posDataset.add(new LabeledExample<Double, SparseVector>(
                le.getLabel().equals(1d) ? 1d : -1d,
                le.getExample()));
        }
        posClassifier = createModel();
        posClassifier.train(posDataset);

        LabeledDataset<Double, SparseVector> negDataset = new LabeledDataset<Double, SparseVector>();
        for (LabeledExampleEntry<Double, SparseVector> le : trainDataset) {
            negDataset.add(new LabeledExample<Double, SparseVector>(
                le.getLabel().equals(-1d) ? -1d : 1d,
                le.getExample()));
        }
        negClassifier = createModel();
        negClassifier.train(negDataset);

        posSortedScores = negSortedScores = null;
        exampleScores = Lists.newArrayList();
        for (LabeledExampleEntry<Double, SparseVector> le : trainDataset) {
            Prediction<Double> posPrediction = posClassifier.predict(le.getExample());
            Prediction<Double> negPrediction = negClassifier.predict(le.getExample());
            exampleScores.add(new ExampleScore(
                le.getLabel(),
                Math.signum(posPrediction.getBest().getLabel()) * posPrediction.getBest().getScore(),
                Math.signum(negPrediction.getBest().getLabel()) * negPrediction.getBest().getScore()));
        }
    }

    @Override
    public Prediction<T> predict(SparseVector example) {
        Prediction<Double> posPred = posClassifier.predict(example);
        Prediction<Double> negPred = negClassifier.predict(example);

        double negPredLabel = negPred.getBest().getLabel();
        double posPredLabel = posPred.getBest().getLabel();

        // flip over predictions with score falling under bias
        Double negScore = null, posScore = null;
        if (biasToPosRate != null && biasToPosRate > 0) {
            if (posPredLabel < 0) {
                double percentile = getPercentileScore(posPred.getBest().getScore(), true);
                if (percentile < biasToPosRate) {
                    posPredLabel = 1;
                    posScore = biasToPosRate - percentile;
                }
            }
        } else if (biasToPosRate != null && biasToPosRate < 0) {
            if (posPredLabel > 0) {
                double score = getPercentileScore(posPred.getBest().getScore(), true);
                if (score < -biasToPosRate) {
                    posPredLabel = -1;
                    posScore = biasToPosRate + score;
                }
            }
        }
        if (biasToNegRate != null && biasToNegRate > 0) {
            if (negPredLabel > 0) {
                double score = getPercentileScore(negPred.getBest().getScore(), false);
                if (score < biasToNegRate) {
                    negPredLabel = -1;
                    negScore = biasToNegRate - score;
                }
            }
        } else if (biasToNegRate != null && biasToNegRate < 0) {
            if (negPredLabel < 0) {
                double score = getPercentileScore(negPred.getBest().getScore(), false);
                if (score < -biasToNegRate) {
                    negPredLabel = 1;
                    negScore = biasToNegRate + score;
                }
            }
        }

        // determine label, calc percentile scores if required
        double bestLabel = negPredLabel == posPredLabel ? negPredLabel : 0;
        posScore = posScore != null ? posScore :
            (scorePercentile ? getPercentileScore(posPred.getBest().getScore(), true) : posPred.getBest().getScore());
        negScore = negScore != null ? negScore :
            (scorePercentile ? getPercentileScore(negPred.getBest().getScore(), false) : negPred.getBest().getScore());

        double bestScore = Math.min(posScore, negScore);
        // double bestScore = (posScore + negScore) / 2;

        return new Prediction<T>(new PredictionScore<T>(bestScore,
            bestLabel == 0 ? neutralLabel : (bestLabel > 0 ? positiveLabel : negativeLabel)));
    }

    protected LinearModel createModel() {
        return new LinearModel(LinearModels.SVM_CLASSIFIER.getDefaultParameter());
    }

    private double getPercentileScore(double score, boolean isPosScores) {
        List<Double> scores;
        if (isPosScores) {
            if (posSortedScores == null) {
                posSortedScores = Lists.newArrayList();
                for (ExampleScore example : exampleScores) {
                    posSortedScores.add(example.getPosScore());
                }
                Collections.sort(posSortedScores);
            }
            scores = posSortedScores;
        } else {
            if (negSortedScores == null) {
                negSortedScores = Lists.newArrayList();
                for (ExampleScore example : exampleScores) {
                    negSortedScores.add(example.getNegScore());
                }
                Collections.sort(negSortedScores);
            }
            scores = negSortedScores;
        }
        return (double) Math.abs(Collections.binarySearch(scores, score)) / scores.size();
    }

    public static class ExampleScore implements Serializable
    {
        private static final long serialVersionUID = -6547176247657292269L;

        private final double Label;
        private final double PosScore;
        private final double NegScore;

        public ExampleScore(double label, double posScore, double negScore) {
            Label = label;
            PosScore = posScore;
            NegScore = negScore;
        }

        public double getLabel() {
            return Label;
        }

        public double getPosScore() {
            return PosScore;
        }

        public double getNegScore() {
            return NegScore;
        }
    }
}
