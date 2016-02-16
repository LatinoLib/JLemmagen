package org.latinolib.eval;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.latinolib.model.LabeledDataset;
import org.latinolib.model.LabeledExampleEntry;
import org.latinolib.model.Model;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.concurrent.*;

/**
 * Author saxo
 */
public class CrossValidator<T, U>
{
    private final int numFolds;
    private final LabeledDataset<T, U> data;
    private final boolean stratified;
    private final boolean shuffled;
    private final List<FoldData<T, U>> folds;

    public CrossValidator(int numFolds, LabeledDataset<T, U> data, Random rnd, boolean shuffled, boolean stratified) {
        Preconditions.checkArgument(data.size() >= 2);
        Preconditions.checkArgument(numFolds >= 2 && numFolds <= data.size());

        this.numFolds = numFolds;
        this.data = new LabeledDataset<T, U>(Preconditions.checkNotNull(data)); // defensive copy
        this.stratified = stratified;
        this.shuffled = shuffled;
        folds = Lists.newArrayListWithCapacity(numFolds);
        for (int i = 0; i < numFolds; i++) { folds.add(null); }
        if (shuffled || stratified) {
            if (stratified) {
                groupByLabel(this.data, shuffled, rnd);
            } else {
                Collections.shuffle(this.data, rnd == null ? new Random(1) : rnd);
            }
        }
    }

    public static <T, U> CrossValidator<T, U> standard(int numFolds, LabeledDataset<T, U> data) {
        return new CrossValidator<T, U>(numFolds, data, null, true, false);
    }

    public static <T, U> CrossValidator<T, U> stratified(int numFolds, LabeledDataset<T, U> data) {
        return new CrossValidator<T, U>(numFolds, data, null, true, true);
    }

    public static <T, U> CrossValidator<T, U> standard(int numFolds, LabeledDataset<T, U> data,
            boolean shuffle, Random rnd) {
        return new CrossValidator<T, U>(numFolds, data, rnd, shuffle, false);
    }

    public static <T, U> CrossValidator<T, U> stratified(int numFolds, LabeledDataset<T, U> data,
            boolean shuffle, Random rnd) {
        return new CrossValidator<T, U>(numFolds, data, rnd, shuffle, true);
    }

    public static <T, U> List<Model<T, U>> getModelList(Supplier<Model<T, U>> modelSupplier, int size) {
        Preconditions.checkNotNull(modelSupplier);
        Preconditions.checkArgument(size > 0);
        List<Model<T, U>> models = Lists.newArrayListWithCapacity(size);
        for (int i = 0; i < size; i++) {
            models.add(modelSupplier.get());
        }
        return models;
    }

    public int getNumFolds() {
        return numFolds;
    }

    public boolean isStratified() {
        return stratified;
    }

    public boolean isShuffled() {
        return shuffled;
    }

    public FoldData<T, U> getFold(int fold) {
        Preconditions.checkArgument(fold >= 1 && fold <= numFolds);
        int idx = fold - 1;
        if (folds.get(idx) == null) {
            FoldData<T, U> foldData = stratified ? splitStratified(numFolds, fold, data) : split(numFolds, fold, data);
            synchronized (folds) {
                if (folds.get(idx) == null) {
                    folds.set(idx, foldData);
                }
            }
        }
        return folds.get(idx);
    }

    public List<FoldData<T, U>> getFolds() {
        return Lists.newArrayList(
            new Iterator<FoldData<T, U>>()
            {
                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < numFolds;
                }

                @Override
                public FoldData<T, U> next() {
                    return getFold(current++ + 1);
                }

                @Override
                public void remove() {
                    throw new NotImplementedException();
                }
            }
        );
    }

    public PerfData<T> runModel(List<Model<T, U>> models) {
        return runModel(models, "", "");
    }

    public PerfData<T> runModel(final List<Model<T, U>> models, String expName, String algName) {
        try {
            return runModel(models, expName, algName, Executors.newSingleThreadExecutor());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PerfData<T> runModel(List<Model<T, U>> models, ExecutorService executor)
            throws ExecutionException, InterruptedException {
        return runModel(models, "", "", executor);
    }

    public PerfData<T> runModel(final List<Model<T, U>> models, final String expName, final String algName,
            ExecutorService executor) throws ExecutionException, InterruptedException {
        Preconditions.checkNotNull(models);
        Preconditions.checkArgument(models.size() >= numFolds);
        Preconditions.checkNotNull(executor);

        final PerfData<T> perfData = new PerfData<T>();
        List<Future<?>> futures = Lists.newArrayList();
        for (int i = 0; i < numFolds; i++) {
            final int foldN = i + 1;
            futures.add(executor.submit(new Runnable()
            {
                @Override
                public void run() {
                    Model<T, U> model = models.get(foldN - 1);
                    FoldData<T, U> fold = getFold(foldN);
                    model.train(fold.getTrainSet());
                    PerfMatrix<T> matrix = perfData.getPerfMatrix(expName, algName, foldN);
                    for (LabeledExampleEntry<T, U> le : fold.getTestSet()) {
                        T label = model.predict(le.getExample()).getBest().getLabel();
                        matrix.addCount(le.getLabel(), label);
                    }
                }
            }));
        }
        for (Future<?> future : futures) {
            future.get();
        }
        return perfData;
    }

    public static <T, U> Map<T, List<LabeledExampleEntry<T, U>>> getLabelGroups(LabeledDataset<T, U> data) {
        Preconditions.checkNotNull(data);
        Map<T, List<LabeledExampleEntry<T, U>>> groups = new HashMap<T, List<LabeledExampleEntry<T, U>>>();
        for (LabeledExampleEntry<T, U> le : data) {
            List<LabeledExampleEntry<T, U>> examples = groups.get(le.getLabel());
            if (examples == null) {
                groups.put(le.getLabel(), examples = Lists.newArrayList());
            }
            examples.add(le);
        }
        return groups;
    }

    public static <T, U> void groupByLabel(LabeledDataset<T, U> data, boolean shuffle, Random rnd) {
        Map<T, List<LabeledExampleEntry<T, U>>> groups = getLabelGroups(data);
        data.clear();
        for (Map.Entry<T, List<LabeledExampleEntry<T, U>>> g : groups.entrySet()) {
            List<LabeledExampleEntry<T, U>> list = g.getValue();
            if (shuffle) {
                if (rnd == null) {
                    Collections.shuffle(list);
                } else {
                    Collections.shuffle(list, rnd);
                }
            }
            data.addAll(list);
        }
    }

    public static <T, U> FoldData<T, U> splitStratified(int numFolds, int fold, LabeledDataset<T, U> data) {
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(data.size() >= 2);
        Preconditions.checkArgument(numFolds >= 2 && numFolds <= data.size());
        Preconditions.checkArgument(fold >= 1 && fold <= numFolds);

        // calc label segments
        Map<T, Integer> labelSegments = new HashMap<T, Integer>();
        T label = data.get(0).getLabel();
        for (int i = 1, startN = 0; i <= data.size(); i++) {
            if (i == data.size() || !label.equals(data.get(i).getLabel())) {
                if (labelSegments.containsKey(label)) {
                    throw new IllegalArgumentException("labeled data items not grouped");
                }
                labelSegments.put(label, i - startN);
                if (i < data.size()) {
                    startN = i;
                    label = data.get(i).getLabel();
                }
            }
        }
        if (data.size() < numFolds * labelSegments.size()) {
            throw new IllegalArgumentException("dataset too small to stratify");
        }

        // populate sets
        LabeledDataset<T, U> trainSet = new LabeledDataset<T, U>();
        LabeledDataset<T, U> testSet = new LabeledDataset<T, U>();
        int segStart = 0;
        for (Map.Entry<T, Integer> segment : labelSegments.entrySet()) {
            int len = segment.getValue() / numFolds;
            int testStart = segStart + (fold - 1) * len;
            int mod = segment.getValue() % numFolds;
            if (fold <= mod) {
                len++;
                testStart += fold - 1;
            } else {
                testStart += mod;
            }
            int testEnd = testStart + len;

            for (int i = segStart; i < testStart; i++) {
                trainSet.add(data.get(i).getLabel(), data.get(i).getExample());
            }
            for (int i = testStart; i < testEnd; i++) {
                testSet.add(data.get(i).getLabel(), data.get(i).getExample());
            }
            int segEnd = segStart + segment.getValue();
            for (int i = testEnd; i < segEnd; i++) {
                trainSet.add(data.get(i).getLabel(), data.get(i).getExample());
            }
            segStart = segEnd;
        }
        return new FoldData<T, U>(trainSet, testSet);
    }

    public static <T, U> FoldData<T, U> split(int numFolds, int fold, LabeledDataset<T, U> data) {
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(data.size() >= 2);
        Preconditions.checkArgument(numFolds >= 2 && numFolds <= data.size());
        Preconditions.checkArgument(fold >= 1 && fold <= numFolds);

        LabeledDataset<T, U> trainSet = new LabeledDataset<T, U>();
        LabeledDataset<T, U> testSet = new LabeledDataset<T, U>();
        double step = (double) data.size() / numFolds;
        double d = 0;
        for (int i = 0; i < numFolds; i++, d += step) {
            int endJ = (int) Math.round(d + step);
            if (i == fold - 1) {
                for (int j = (int) Math.round(d); j < endJ; j++) {
                    testSet.add(data.get(j).getLabel(), data.get(j).getExample());
                }
            } else {
                for (int j = (int) Math.round(d); j < endJ; j++) {
                    trainSet.add(data.get(j).getLabel(), data.get(j).getExample());
                }
            }
        }
        return new FoldData<T, U>(trainSet, testSet);
    }

    public static class FoldData<T, U>
    {
        private final LabeledDataset<T, U> trainSet;
        private final LabeledDataset<T, U> testSet;

        public FoldData(LabeledDataset<T, U> trainSet, LabeledDataset<T, U> testSet) {
            this.trainSet = trainSet;
            this.testSet = testSet;
        }

        public LabeledDataset<T, U> getTrainSet() {
            return trainSet;
        }

        public LabeledDataset<T, U> getTestSet() {
            return testSet;
        }
    }
}
