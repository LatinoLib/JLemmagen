package org.latinolib.eval;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author saxo
 */
public class PerfMatrix<T>
{
    private final ConcurrentMap<T, ConcurrentMap<T, Integer>> data =
        new ConcurrentHashMap<T, ConcurrentMap<T, Integer>>();
    private final Set<T> labels = new HashSet<T>();

    public PerfMatrix() {
    }

    public void addLabels(Collection<T> labels) {
        synchronized (this.labels) {
            this.labels.addAll(labels);
        }
    }

    public boolean removeLabel(T label) {
        synchronized (labels) {
            return labels.remove(label);
        }
    }

    public void addCount(T actual, T predicted, int count) {
        synchronized (labels) {
            labels.add(actual);
            labels.add(predicted);
        }
        synchronized (data) {
            ConcurrentMap<T, Integer> row = data.get(actual);
            if (row == null) {
                row = new ConcurrentHashMap<T, Integer>();
                data.put(actual, row);
            }
            Integer oldCount = row.get(predicted);
            row.put(predicted, oldCount == null ? count : oldCount + count);
        }
    }

    public void addCount(T actual, T predicted) {
        addCount(actual, predicted, 1);
    }

    public Set<T> getLabels() {
        return Collections.unmodifiableSet(labels);
    }

    public int get(T actual, T predicted) {
        synchronized (data) {
            Map<T, Integer> row = data.get(actual);
            if (row != null) {
                Integer count = row.get(predicted);
                return count == null ? 0 : count;
            }
            return 0;
        }
    }

    public int getActual(T label) {
        return sumRow(label);
    }

    public int getPredicted(T label) {
        return sumCol(label);
    }

    public void reset() {
        synchronized (data) {
            data.clear();
        }
        synchronized (labels) {
            labels.clear();
        }
    }

    private int sumRow(T label) {
        int sum = 0;
        Map<T, Integer> row = data.get(label);
        if (row != null) {
            for (Integer c : row.values()) {
                sum += c;
            }
        }
        return sum;
    }

    private int sumCol(T label) {
        int sum = 0;
        for (Map<T, Integer> row : data.values()) {
            Integer c = row.get(label);
            if (c != null) {
                sum += c;
            }
        }
        return sum;
    }

    public int getSumAll() {
        int sum = 0;
        for (Map<T, Integer> row : data.values()) {
            for (int val : row.values()) {
                sum += val;
            }
        }
        return sum;
    }

    public int getSumDiag() {
        int sum = 0;
        for (Map.Entry<T, ConcurrentMap<T, Integer>> entry : data.entrySet()) {
            Integer count = entry.getValue().get(entry.getKey());
            if (count != null) {
                sum += count;
            }
        }
        return sum;
    }

    public double getPrecision(T label) {
        RatioCountPair precisionCount = getPrecisionCount(label);
        return precisionCount.getRatio() / precisionCount.getCount();
    }

    public RatioCountPair getPrecisionCount(T label) {
        return new RatioCountPair(get(label, label), sumCol(label));
    }

    public double getRecall(T label) {
        RatioCountPair recallCount = getRecallCount(label);
        return recallCount.getRatio() / recallCount.getCount();
    }

    public RatioCountPair getRecallCount(T label) {
        return new RatioCountPair(get(label, label), sumRow(label));
    }

    public double getF(double w, T label) {
        double p = getPrecision(label);
        double r = getRecall(label);
        return (w * w + 1) * p * r / (w * w * p + r);
    }

    public double getF1(T label) {
        return getF(1, label);
    }

    public int getActualSum(T label) {
        return sumRow(label);
    }

    public double getActualRatio(T label) {
        return (double) getActualSum(label) / getSumAll();
    }

    public int getPredictedSum(T label) {
        return sumCol(label);
    }

    public double getPredictedRatio(T label) {
        return (double) getPredictedSum(label) / getSumAll();
    }


    // *** Micro-averaging (over examples) ***

    public double getAccuracy() {
        return (double) getSumDiag() / getSumAll();
    }

    public double getError() {
        return 1 - getAccuracy();
    }

    public double getMicroPrecision() {
        double result = 0;
        for (T label : labels) {
            result += getActual(label) * getPrecision(label);
        }
        return result / getSumAll();
    }

    public double getMicroRecall() {
        double result = 0;
        for (T label : labels) {
            result += getActual(label) * getRecall(label);
        }
        return result / getSumAll();
    }

    public double getMicroF1() {
        double result = 0;
        for (T label : labels) {
            result += getActual(label) * getF1(label);
        }
        return result / getSumAll();
    }


    // *** Macro-averaging (over classes) ***

    public double getMacroPrecision() {
        double sum = 0;
        for (T label : labels) {
            sum += getPrecision(label);
        }
        return sum / labels.size();
    }

    public double getMacroRecall() {
        double sum = 0;
        for (T label : labels) {
            sum += getRecall(label);
        }
        return sum / labels.size();
    }

    public double getMacroF(double w) {
        double sum = 0;
        for (T label : labels) {
            sum += getF(w, label);
        }
        return sum / labels.size();
    }

    public double getMacroF1() {
        double sum = 0;
        for (T label : labels) {
            sum += getF1(label);
        }
        return sum / labels.size();
    }

    public double getScore(PerfMetric metric) {
        switch (metric) {
            case ACCURACY:
                return getAccuracy();
            case MICRO_PRECISION:
                return getMicroPrecision();
            case MICRO_RECALL:
                return getMicroRecall();
            case MICRO_F1:
                return getMicroF1();
            case MACRO_PRECISION:
                return getMacroPrecision();
            case MACRO_RECALL:
                return getMacroRecall();
            case MACRO_F1:
                return getMacroF1();
            case ERROR:
                return getError();
            case K_ALPHA_NOMINAL:
                return getKAlpha();
            case ACC_STD_ERROR_CONF_90:
                return getAccStdError(0.9);
            case ACC_STD_ERROR_CONF_95:
                return getAccStdError(0.95);
            case ACC_STD_ERROR_CONF_99:
                return getAccStdError(0.99);
            default:
                throw new IllegalArgumentException("metric");
        }
    }


    public double getScore(ClassPerfMetric metric, T label) {
        switch (metric) {
            case PRECISION:
                return getPrecision(label);
            case RECALL:
                return getRecall(label);
            case F1:
                return getF1(label);
            case PREDICTED_COUNT:
                return getPredictedSum(label);
            case PREDICTED_RATIO:
                return getPredictedRatio(label);
            case ACTUAL_COUNT:
                return getActualSum(label);
            case ACTUAL_RATIO:
                return getActualRatio(label);
            default:
                throw new IllegalArgumentException("metric");
        }
    }

    public String toString() {
        List<T> labels = Lists.newArrayList(this.labels);
        Collections.sort(labels, new Comparator<T>()
        {
            @Override
            public int compare(T o1, T o2) {
                Preconditions.checkNotNull(o1);
                Preconditions.checkNotNull(o2);
                return o1.toString().compareTo(o2.toString());
            }
        });

        StringBuilder str = new StringBuilder();
        int all = getSumAll();

        int len = 0;
        for (T l : labels) {
            len = Math.max(l.toString().length(), len);
        }
        len = Math.max(Math.max(Integer.toString(all).length(), 11), len) + 2;

        str.append(Strings.padEnd("", len, ' '));
        str.append(Strings.padStart("|", 4, ' '));
        for (T predicted : labels) {
            str.append(Strings.padStart(predicted.toString(), len, ' '));
        }
        str.append(Strings.padStart("|", 4, ' '));
        str.append(Strings.padStart("sum actual", len, ' '));
        str.append(Strings.padStart("%", len, ' '));
        str.append('\n');
        str.append(new String(new char[(labels.size() + 3) * len + 8]).replace("\0", "-"));
        str.append('\n');

        for (T actual : labels) {
            str.append(Strings.padStart(actual.toString(), len, ' '));
            str.append(Strings.padStart("|", 4, ' '));
            for (T predicted : labels) {
                str.append(Strings.padStart(Integer.toString(get(actual, predicted)), len, ' '));
            }
            str.append(Strings.padStart("|", 4, ' '));
            str.append(Strings.padStart(Integer.toString(sumRow(actual)), len, ' '));
            str.append(Strings.padStart(String.format("%.1f", (double) sumRow(actual) / all), len, ' '));
            str.append("\n");
        }

        str.append(new String(new char[(labels.size() + 3) * len + 8]).replace("\0", "-"));
        str.append("\n");
        str.append(Strings.padStart("sum predicted", len, ' '));
        str.append(Strings.padStart("|", 4, ' '));
        for (T predicted : labels) {
            str.append(Strings.padStart(Integer.toString(sumCol(predicted)), len, ' '));
        }
        str.append(Strings.padStart("|", 4, ' '));
        str.append(Strings.padStart(Integer.toString(all), len, ' '));
        str.append("\n");

        str.append(Strings.padStart("%", len, ' '));
        str.append(Strings.padStart("|", 4, ' '));
        for (T predicted : labels) {
            str.append(Strings.padStart(String.format("%.1f", (double) sumCol(predicted) / all), len, ' '));
        }
        str.append("\n");

        return str.toString();
    }

    // General metrices
    public String toStringPerf(List<PerfMetric> perfMetrics) // empty for all metrics
    {
        Preconditions.checkNotNull(perfMetrics);
        if (Preconditions.checkNotNull(perfMetrics).size() == 0) {
            perfMetrics = Lists.newArrayList(PerfMetric.values());
        }

        StringBuilder str = new StringBuilder();

        str.append("\nGeneral metrices:\n");
        for (PerfMetric perfMetric : perfMetrics) {
            str.append(Strings.padStart(perfMetric.toString(), 30, ' '));
            str.append(Strings.padStart(String.format("%.3f", getScore(perfMetric)), 6, ' '));
            str.append("\n");
        }
        return str.toString();
    }

    // class-specific metrics
    public String toStringClass(List<ClassPerfMetric> classMetrics) // metrices = empty for all metrics
    {
        if (Preconditions.checkNotNull(classMetrics).size() == 0) {
            classMetrics = Lists.newArrayList(ClassPerfMetric.values());
        }

        List<T> labels = Lists.newArrayList(this.labels);
        Collections.sort(labels, new Comparator<T>()
        {
            @Override
            public int compare(T o1, T o2) {
                Preconditions.checkNotNull(o1);
                Preconditions.checkNotNull(o2);
                return o1.toString().compareTo(o2.toString());
            }
        });

        StringBuilder str = new StringBuilder();
        str.append("\nClass-specific metrices:\n");

        int len = 0;
        for (T label : labels) {
            len = Math.max(len, label.toString().length());
        }
        len = Math.max(len, 13) + 2;

        str.append(Strings.padStart("", len, ' '));
        for (ClassPerfMetric metric : classMetrics) {
            str.append(Strings.padStart(metric.toString(), len, ' '));
        }
        str.append("\n");
        for (T label : labels) {
            str.append(Strings.padStart(label.toString(), len, ' '));
            for (ClassPerfMetric metric : classMetrics) {
                str.append(Strings.padStart(String.format("%.3f", getScore(metric, label)), len, ' '));
            }
            str.append("\n");
        }
        return str.toString();
    }

    // Ordinal regression metrics
    public String toStringOrdinal(List<T> orderedLabels, List<OrdinalPerfMetric> ordinalMetrics) {
        if (ordinalMetrics == null || ordinalMetrics.size() == 0) {
            ordinalMetrics = Lists.newArrayList(OrdinalPerfMetric.values());
        }

        StringBuilder str = new StringBuilder();
        str.append("\nOrdinal metrices:\n");
        List<T> labels = Lists.newArrayList(this.labels);
        for (OrdinalPerfMetric metric : ordinalMetrics) {
            str.append(Strings.padStart(metric.toString(), 30, ' '));
            str.append(Strings.padStart(String.format("%.3f", getScore(metric, labels)), 6, ' '));
        }
        return str.toString();
    }


    // *** Ordinal regression measures ***

    public double getError(List<T> orderedLabels, Map<T, Map<T, Double>> weights) {
        Preconditions.checkNotNull(orderedLabels);
        Preconditions.checkNotNull(weights);
        Preconditions.checkArgument(orderedLabels.containsAll(this.labels) && this.labels.containsAll(orderedLabels));

        double sum = 0;
        for (T actual : this.labels) {
            for (T predicted : this.labels) {
                sum += get(actual, predicted) * weights.get(actual).get(predicted);
            }
        }
        return sum / getSumAll();
    }

    public double getKAlpha() {
        List<T> labels = Lists.newArrayList(this.labels);
        return getKrippendorffsAlpha(labels, getErrorXWeights(labels));
    }

    public double getAccStdError(double confidenceLevel) {
        double zScore = StdErrTables.getZScore(confidenceLevel);
        double acc = getAccuracy();
        return zScore * Math.sqrt(acc * (1 - acc) / getSumAll());
    }

    // implementation of http://vassarstats.net/kappaexp.html and http://vassarstats.net/kappa.html
    // correction from wikipedia Krippendorff's Alpha
    public double getKrippendorffsAlpha(List<T> orderedLabels, Map<T, Map<T, Double>> weights) {
        Preconditions.checkNotNull(orderedLabels);
        Preconditions.checkArgument(this.labels.containsAll(orderedLabels) && orderedLabels.containsAll(this.labels));

        // the observed matrix
        double s = getSumAll();
        Map<T, Map<T, Double>> observed = new HashMap<T, Map<T, Double>>();
        for (T actual : orderedLabels) {
            Map<T, Double> row = new HashMap<T, Double>();
            for (T predicted : orderedLabels) {
                row.put(predicted, get(actual, predicted) / s);
            }
            observed.put(actual, row);
        }

        // the matrix of expected values
        Map<T, Map<T, Double>> expected = new HashMap<T, Map<T, Double>>();
        for (T actual : orderedLabels) {
            Map<T, Double> row = new HashMap<T, Double>();
            for (T predicted : orderedLabels) {
                row.put(predicted, getActual(actual) / s * getPredicted(predicted) / (s - 1)); // a[i] * p[j] / s / (s - 1))
            }
            expected.put(actual, row);
        }

        // the weights matrix
        weights = new HashMap<T, Map<T, Double>>(weights); // modify the copy
        for (T actual : orderedLabels) {
            for (T predicted : orderedLabels) {
                weights.get(actual).put(predicted, 1 - weights.get(actual).get(predicted));
            }
        }

        double s1 = 0, s2 = 0;
        for (T actual : orderedLabels) {
            for (T predicted : orderedLabels) {
                s1 += weights.get(actual).get(predicted) * observed.get(actual).get(predicted);
                s2 += weights.get(actual).get(predicted) * expected.get(actual).get(predicted);
            }
        }
        return 1 - (1 - s1) / (1 - s2);
    }

    public double getF1AvgExtremeClasses(List<T> orderedLabels) {
        T label1 = orderedLabels.get(0);
        T label2 = orderedLabels.get(orderedLabels.size() - 1);
        return (getF1(label1) + getF1(label2)) / 2;
    }

    public double getScore(OrdinalPerfMetric metric, List<T> orderedLabels) {
        Preconditions.checkNotNull(orderedLabels);
        switch (metric) {
            case MEAN_ABSOLUTE_ERROR:
                return getError(orderedLabels, getLinearWeights(orderedLabels, false));
            case MEAN_SQUARED_ERROR:
                return getError(orderedLabels, getSquareWeights(orderedLabels, false));
            case ERROR_TOLERANCE_1:
                return getError(orderedLabels, getErrorXWeights(orderedLabels, 1));
            case ACCURACY_TOLERANCE_1:
                return 1 - getError(orderedLabels, getErrorXWeights(orderedLabels, 1));
            case MEAN_ABSOLUTE_ERROR_NORMALIZED_1:
                return getError(orderedLabels, getLinearWeights(orderedLabels, true));
            case MEAN_SQUARED_ERROR_NORMALIZED_1:
                return getError(orderedLabels, getSquareWeights(orderedLabels, true));
            case K_ALPHA_LINEAR:
                return getKrippendorffsAlpha(orderedLabels, getLinearWeights(orderedLabels, true));
            case K_ALPHA_INTERVAL:
                return getKrippendorffsAlpha(orderedLabels, getSquareWeights(orderedLabels, true));
            case F1_AVG_EXTREME_CLASSES:
                return getF1AvgExtremeClasses(orderedLabels);
            default:
                throw new IllegalArgumentException("invalid ordered metric");
        }
    }


    // weight matrices

    public Map<T, Map<T, Double>> getZeroMatrix(Set<T> labels) {
        Preconditions.checkNotNull(labels);
        Map<T, Map<T, Double>> result = new HashMap<T, Map<T, Double>>();
        for (T row : labels) {
            Map<T, Double> map = new HashMap<T, Double>();
            for (T column : labels) {
                map.put(column, 0.0);
            }
            result.put(row, map);
        }
        return result;
    }

    public Map<T, Map<T, Double>> getErrorXWeights(List<T> orderedLabels) {
        return getErrorXWeights(orderedLabels, 0);
    }

    public Map<T, Map<T, Double>> getErrorXWeights(List<T> orderedLabels, int tolerance) {
        Preconditions.checkNotNull(orderedLabels);
        Map<T, Map<T, Double>> weights = getZeroMatrix(new HashSet<T>(orderedLabels));
        for (int i = 0; i < orderedLabels.size(); i++) {
            for (int j = 0; j < orderedLabels.size(); j++) {
                weights.get(orderedLabels.get(i)).put(orderedLabels.get(j), Math.abs(i - j) > tolerance ? 1d : 0d);
            }
        }
        return weights;
    }

    public Map<T, Map<T, Double>> getLinearWeights(List<T> orderedLabels, boolean normalize) {
        Preconditions.checkNotNull(orderedLabels);
        Map<T, Map<T, Double>> weights = getZeroMatrix(new HashSet<T>(orderedLabels));
        double step = 1;
        if (normalize) {
            step = 1.0 / (orderedLabels.size() - 1);
        }
        for (int i = 0; i < orderedLabels.size(); i++) {
            for (int j = 0; j < orderedLabels.size(); j++) {
                weights.get(orderedLabels.get(i)).put(orderedLabels.get(j), Math.abs(i * step - j * step));
            }
        }
        return weights;
    }

    public Map<T, Map<T, Double>> getSquareWeights(List<T> orderedLabels) {
        return getSquareWeights(orderedLabels, false);
    }

    public Map<T, Map<T, Double>> getSquareWeights(List<T> orderedLabels, boolean normalize) {
        Preconditions.checkNotNull(orderedLabels);

        Map<T, Map<T, Double>> weights = getLinearWeights(orderedLabels, normalize);
        for (T labelAct : orderedLabels) {
            for (T labelPred : orderedLabels) {
                weights.get(labelAct).put(labelPred,
                    weights.get(labelAct).get(labelPred) * weights.get(labelAct).get(labelPred));
            }
        }
        return weights;
    }

    public static class RatioCountPair
    {
        private final double ratio;
        private final int count;

        public RatioCountPair(double ratio, int count) {
            this.ratio = ratio;
            this.count = count;
        }

        public double getRatio() {
            return ratio;
        }

        public int getCount() {
            return count;
        }
    }
}