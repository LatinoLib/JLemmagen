package org.latinolib.eval;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author saxo
 */
public class PerfData<T>
{
    private class FoldData extends ArrayList<PerfMatrix<T>>
    {
        private static final long serialVersionUID = -7820683828561338945L;
        private transient final Object lock = new Object();

        public void resize(int n) {
            synchronized (lock) {
                while (size() < n) {
                    add(null);
                }
            }
        }

        public void put(int foldNum, PerfMatrix<T> mtx) {
            resize(foldNum);
            super.add(foldNum - 1, mtx);
        }
    }

    private final ConcurrentMap<String, ConcurrentMap<String, FoldData>> data =
        new ConcurrentHashMap<String, ConcurrentMap<String, FoldData>>();

    public void reset() {
        data.clear();
    }

    public static class FoldKeyPair
    {
        private final String expName;
        private final String algName;

        public String getExpName() {
            return expName;
        }

        public String getAlgName() {
            return algName;
        }

        public FoldKeyPair(String expName, String algName) {

            this.expName = expName;
            this.algName = algName;
        }
    }

    public List<FoldKeyPair> getDataKeys() {
        List<FoldKeyPair> result = Lists.newArrayList();
        for (Map.Entry<String, ConcurrentMap<String, FoldData>> expEntry : data.entrySet()) {
            for (Map.Entry<String, FoldData> algEntry : expEntry.getValue().entrySet()) {
                result.add(new FoldKeyPair(expEntry.getKey(), algEntry.getKey()));
            }
        }
        return result;
    }

    public PerfMatrix<T> getPerfMatrix(String expName, String algName, int foldNum) {
        return setPerfMatrix(expName, algName, foldNum, null);
    }

    public PerfMatrix<T> setPerfMatrix(String expName, String algName, int foldNum, PerfMatrix<T> matrix) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        Preconditions.checkArgument(foldNum >= 1);

        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData == null) {
            algData = new ConcurrentHashMap<String, FoldData>();
            ConcurrentMap<String, FoldData> prev = data.putIfAbsent(expName, algData);
            if (prev != null) {
                algData = prev;
            }
        }
        FoldData foldData = algData.get(algName);
        if (foldData == null) {
            foldData = new FoldData();
            FoldData prev = algData.putIfAbsent(algName, foldData);
            if (prev != null) {
                foldData = prev;
            }
        }
        foldData.resize(foldNum);
        if (matrix == null) {
            matrix = foldData.get(foldNum - 1);
            if (matrix == null) {
                matrix = new PerfMatrix<T>();
                foldData.put(foldNum - 1, matrix);
            }
        } else {
            foldData.put(foldNum - 1, matrix);
        }
        return matrix;
    }

    public PerfMatrix<T> getSumPerfMatrix(String expName, String algName) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            if (foldData != null) {
                PerfMatrix<T> sumMtx = new PerfMatrix<T>();
                for (PerfMatrix<T> mtx : foldData) {
                    if (mtx != null) {
                        Set<T> labels = mtx.getLabels();
                        for (T actual : labels) {
                            for (T predicted : labels) {
                                sumMtx.addCount(actual, predicted, mtx.get(actual, predicted));
                            }
                        }
                    }
                }
                return sumMtx;
            }
        }
        return null;
    }

    public int getFoldCount(String expName, String algName) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);

        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            return foldData == null ? 0 : foldData.size();
        }
        return 0;
    }

    public Set<T> GetLabels(String expName, String algName) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            if (foldData != null) {
                Set<T> labels = new HashSet<T>();
                for (PerfMatrix<T> foldMtx : foldData) {
                    if (foldMtx != null) {
                        labels.addAll(foldMtx.getLabels());
                    }
                }
                return labels;
            }
        }
        return null;
    }

    public double getVal(int foldNum, String expName, String algName, PerfMetric metric) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        Preconditions.checkArgument(foldNum >= 1);
        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            if (foldData != null) {
                if (foldNum <= foldData.size() && foldData.get(foldNum - 1) != null) {
                    return foldData.get(foldNum - 1).getScore(metric);
                }
            }
        }
        return Double.NaN;
    }

    public double getVal(int foldNum, String expName, String algName, ClassPerfMetric metric, T lbl) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        Preconditions.checkArgument(foldNum >= 1);
        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            if (foldData != null) {
                if (foldNum <= foldData.size() && foldData.get(foldNum - 1) != null) {
                    return foldData.get(foldNum - 1).getScore(metric, lbl);
                }
            }
        }
        return Double.NaN;
    }

    public double getVal(int foldNum, String expName, String algName, OrdinalPerfMetric metric, List<T> orderedLabels) {
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        Preconditions.checkArgument(foldNum >= 1);
        ConcurrentMap<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            if (foldData != null) {
                if (foldNum <= foldData.size() && foldData.get(foldNum - 1) != null) {
                    return foldData.get(foldNum - 1).getScore(metric, orderedLabels);
                }
            }
        }
        return Double.NaN;
    }

    public AvgStdDevPair getAvgDev(String expName, String algName, final PerfMetric metric) {
        return getAvg(expName, algName,
            new Function<PerfMatrix<T>, Double>()
            {
                @Override
                public Double apply(PerfMatrix<T> input) {
                    return input.getScore(metric);
                }
            });
    }

    public double getAvg(String expName, String algName, PerfMetric metric) {
        return getAvgDev(expName, algName, metric).getAvg();
    }

    public AvgStdDevPair getAvgDev(String expName, String algName, final ClassPerfMetric metric, final T label) {
        return getAvg(expName, algName,
            new Function<PerfMatrix<T>, Double>()
            {
                @Override
                public Double apply(PerfMatrix<T> input) {
                    return input.getScore(metric, label);
                }
            });
    }

    public double getAvg(String expName, String algName, ClassPerfMetric metric, T label) {
        return getAvgDev(expName, algName, metric, label).getAvg();
    }

    public AvgStdDevPair getAvgDev(String expName, String algName, final OrdinalPerfMetric metric, final List<T> orderedLabels) {
        return getAvg(expName, algName,
            new Function<PerfMatrix<T>, Double>()
            {
                @Override
                public Double apply(PerfMatrix<T> input) {
                    return input.getScore(metric, orderedLabels);
                }
            });
    }

    public double getAvg(String expName, String algName, OrdinalPerfMetric metric, List<T> orderedLabels) {
        return getAvgDev(expName, algName, metric, orderedLabels).getAvg();
    }

    public AvgStdDevStdErrTriple getAvgStdErr(String expName, String algName, PerfMetric metric, double confidenceLevel) {
        double zScore = StdErrTables.getZScore(confidenceLevel);
        AvgStdDevPair avg = getAvgDev(expName, algName, metric);
        double sterr = zScore * avg.getStdev() / Math.sqrt(getFoldCount(expName, algName));
        return new AvgStdDevStdErrTriple(avg.getAvg(), avg.getStdev(), sterr);
    }

    public AvgStdDevStdErrTriple getAvgStdErr(String expName, String algName,
                                              ClassPerfMetric metric, T lbl, double confidenceLevel) {
        double zScore = StdErrTables.getZScore(confidenceLevel);
        AvgStdDevPair avg = getAvgDev(expName, algName, metric, lbl);
        double sterr = zScore * avg.getStdev() / Math.sqrt(getFoldCount(expName, algName));
        return new AvgStdDevStdErrTriple(avg.getAvg(), avg.getStdev(), sterr);
    }

    public AvgStdDevStdErrTriple getAvgStdErr(String expName, String algName,
                                              OrdinalPerfMetric metric, List<T> orderedLabels, double confidenceLevel) {
        double zScore = StdErrTables.getZScore(confidenceLevel);
        AvgStdDevPair avg = getAvgDev(expName, algName, metric, orderedLabels);
        double sterr = zScore * avg.getStdev() / Math.sqrt(getFoldCount(expName, algName));
        return new AvgStdDevStdErrTriple(avg.getAvg(), avg.getStdev(), sterr);
    }

    private AvgStdDevPair getAvg(String expName, String algName, Function<PerfMatrix<T>, Double> scoreFunc) {
        Preconditions.checkNotNull(scoreFunc);
        Preconditions.checkNotNull(expName);
        Preconditions.checkNotNull(algName);
        Map<String, FoldData> algData = data.get(expName);
        if (algData != null) {
            FoldData foldData = algData.get(algName);
            if (foldData != null) {
                double sum = 0;
                List<Double> values = Lists.newArrayList();
                for (PerfMatrix<T> foldMtx : foldData) {
                    if (foldMtx != null) {
                        Double result = scoreFunc.apply(foldMtx);
                        if (result != null && result.isNaN() && result.isInfinite()) {
                            sum += result;
                            values.add(result);
                        }
                    }
                }
                double mean = sum / values.size();
                double stdev = 0;
                for (double value : values) {
                    stdev += (value - mean) * (value - mean);
                }
                stdev = Math.sqrt(stdev / values.size());

                return new AvgStdDevPair(mean, stdev);
            }
        }
        return null;
    }

    public static class AvgStdDevPair
    {
        private final double avg;
        private final double stdev;

        public AvgStdDevPair(double avg, double stdev) {

            this.avg = avg;
            this.stdev = stdev;
        }

        public double getAvg() {
            return avg;
        }

        public double getStdev() {
            return stdev;
        }
    }

    public static class AvgStdDevStdErrTriple extends AvgStdDevPair
    {
        private final double sterr;

        public AvgStdDevStdErrTriple(double avg, double stdev, double sterr) {
            super(avg, stdev);
            this.sterr = sterr;
        }

        public double getSterr() {
            return sterr;
        }
    }
}

