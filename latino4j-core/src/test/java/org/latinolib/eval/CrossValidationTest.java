package org.latinolib.eval;

import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.latinolib.model.LabeledDataset;
import org.latinolib.model.LabeledExampleEntry;
import org.latinolib.model.Model;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Author saxo
 */
public class CrossValidationTest
{
    private int datasetSize = 51;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testStratifiedSortedCheckFail() {
        LabeledDataset<Integer, Integer> ld = newData(new Object[][] { {1, 10}, {2, 1}, {1, 1} });

        exception.expect(IllegalArgumentException.class);
        new CrossValidator<Integer, Integer>(2, ld, null, false, true, false).getFolds();
    }

    @Test
    public void testStratifiedGroupedCheckOk() {
        LabeledDataset<Integer, Integer> ld = newData(new Object[][] { { 1, 10 }, { 2, 1 } });
        new CrossValidator<Integer, Integer>(2, ld, null, false, true, false).getFolds();

        ld = newData(new Object[][] { { 1, 10 }, { 2, 1 }, { 1, 1 } });
        new CrossValidator<Integer, Integer>(2, ld, null, true, true, false).getFolds();

        ld = newData(new Object[][] { { 1, 10 }, { 2, 1 }, { 1, 1 }, { 2, 10 } });
        new CrossValidator<Integer, Integer>(2, ld, null, true, true, false).getFolds();
    }

    @Test
    public void testFolding() {
        for (int run = 0; run < 2; run++) {
            boolean stratified = run == 0;
            for (int size = 2; size <= datasetSize; size++) {
                LabeledDataset<Integer, Integer> ld = newData(new Object[][]{ { 1, size } });
                CrossValidator.groupByLabel(ld, true, null);
                for (int numFolds = 2; numFolds <= size; numFolds++) {
                    LabeledDataset<Integer, Integer> aggTestSet = new LabeledDataset<Integer, Integer>();
                    for (int i = 0; i < numFolds; i++) {
                        CrossValidator.FoldData<Integer, Integer> fold = stratified
                            ? CrossValidator.splitStratified(numFolds, i + 1, ld)
                            : CrossValidator.split(numFolds, i + 1, ld);
                        LabeledDataset<Integer, Integer> foldAll = fold.getTestSet();
                        foldAll.addAll(fold.getTrainSet());
                        assertSetEquality(foldAll, ld);
                        aggTestSet.addAll(foldAll);
                    }
                    assertSetEquality(aggTestSet, ld);
                }
            }
        }
    }

    @Test
    public void testStratifiedEvenlyDistributed() {
        int size = datasetSize;
        for (int numLabels = 2; numLabels <= size / 2; numLabels++) {
            Object[][] labelCounts = new Object[numLabels][2];
            for (int label = 1; label <= numLabels; label++) {
                int segSize = size / numLabels;
                if (label <= size % numLabels) {
                    segSize++;
                }
                labelCounts[label - 1][0] = label;
                labelCounts[label - 1][1] = segSize;
            }
            double labelDistr = 1.0 / numLabels;

            LabeledDataset<Integer, Integer> ld = newData(labelCounts);
            CrossValidator.groupByLabel(ld, true, null);

            for (int numFolds = 2; numFolds <= size / numLabels; numFolds++) {
                LabeledDataset<Integer, Integer> aggTestSet = new LabeledDataset<Integer, Integer>();
                for (int i = 0; i < numFolds; i++) {
                    CrossValidator.FoldData<Integer, Integer> fold =
                        CrossValidator.splitStratified(numFolds, i + 1, ld);
                    LabeledDataset<Integer, Integer> foldAll = fold.getTestSet();
                    foldAll.addAll(fold.getTrainSet());
                    assertSetEquality(foldAll, ld);
                    aggTestSet.addAll(foldAll);

                    Map<Integer, List<LabeledExampleEntry<Integer, Integer>>> groups;
                    groups = CrossValidator.getLabelGroups(fold.getTestSet());
                    for (Map.Entry<Integer, List<LabeledExampleEntry<Integer, Integer>>> group : groups.entrySet()) {
                        double ratio = (double)group.getValue().size() / fold.getTestSet().size();
                        assertTrue(Math.abs(labelDistr - ratio) <= 1.0 / fold.getTestSet().size());
                    }

                    groups = CrossValidator.getLabelGroups(fold.getTrainSet());
                    for (Map.Entry<Integer, List<LabeledExampleEntry<Integer, Integer>>> group : groups.entrySet()) {
                        double ratio = (double)group.getValue().size() / fold.getTrainSet().size();
                        assertTrue(Math.abs(labelDistr - ratio) <= 1.0 / fold.getTrainSet().size());
                    }
                }
                assertSetEquality(aggTestSet, ld);
            }
        }
    }

    @Test
    public void testStratifiedUnevenlyDistributed() {
        int size = datasetSize;
        double[] labelDistrs = {0.2, 0.4, 0.1, 0.3};

        Object[][] labelCounts = new Object[labelDistrs.length][2];
        int addedCount = 0;
        for (int label = 1; label <= labelDistrs.length; label++) {
            labelCounts[label - 1][0] = label;
            int labelCount = (int) Math.floor(labelDistrs[label - 1] * size);
            labelCounts[label - 1][1] = labelCount;
            addedCount += labelCount;
        }
        for (int i = 0; i < size - addedCount; i++) {
            int idx = i % labelCounts.length;
            labelCounts[idx][1] = ((Integer)labelCounts[idx][1]) + 1;
            labelDistrs[idx] = (Integer)labelCounts[idx][1] / (double)size;
        }

        LabeledDataset<Integer, Integer> ld = newData(labelCounts);
        CrossValidator.groupByLabel(ld, true, null);

        for (int numFolds = 2; numFolds <= size / labelDistrs.length; numFolds++) {
            LabeledDataset<Integer, Integer> aggTestSet = new LabeledDataset<Integer, Integer>();
            for (int i = 0; i < numFolds; i++) {
                CrossValidator.FoldData<Integer, Integer> fold = CrossValidator.splitStratified(numFolds, i + 1, ld);
                LabeledDataset<Integer, Integer> foldAll = fold.getTestSet();
                foldAll.addAll(fold.getTrainSet());
                assertSetEquality(foldAll, ld);
                aggTestSet.addAll(fold.getTestSet());

                List<Double> test = Lists.newArrayList();
                Map<Integer, List<LabeledExampleEntry<Integer, Integer>>> groups;
                groups = CrossValidator.getLabelGroups(fold.getTestSet());
                for (Map.Entry<Integer, List<LabeledExampleEntry<Integer, Integer>>> group : groups.entrySet()) {
                    double ratio = (double)group.getValue().size() / fold.getTestSet().size();
                    int label = group.getKey();
                    int j = 0;
                    while ((Integer)labelCounts[j][0] != label) { j++; }
                    assertTrue(Math.abs(labelDistrs[j] - ratio) <= 1.0 / fold.getTestSet().size() + 0.00001);
                    test.add((double)group.getValue().size() / fold.getTestSet().size());
                }

                List<Double> train = Lists.newArrayList();
                groups = CrossValidator.getLabelGroups(fold.getTrainSet());
                for (Map.Entry<Integer, List<LabeledExampleEntry<Integer, Integer>>> group : groups.entrySet()) {
                    double ratio = (double)group.getValue().size() / fold.getTrainSet().size();
                    int label = group.getKey();
                    int j = 0;
                    while ((Integer)labelCounts[j][0] != label) { j++; }
                    assertTrue(Math.abs(labelDistrs[j] - ratio) <= 1.0 / fold.getTrainSet().size() + 0.00001);
                    train.add((double)group.getValue().size() / fold.getTrainSet().size());
                }
            }
            assertSetEquality(aggTestSet, ld);
        }
    }

    private static void assertSetEquality(Iterable<LabeledExampleEntry<Integer, Integer>> le1,
            Iterable<LabeledExampleEntry<Integer, Integer>> le2) {
        List<String> set1 = Lists.newArrayList();
        for (LabeledExampleEntry<Integer, Integer> le : le1) {
            set1.add(Integer.toString(le.getLabel()) + " " + Integer.toString(le.getExample()));
        }
        List<String> set2 = Lists.newArrayList();
        for (LabeledExampleEntry<Integer, Integer> le : le2) {
            set2.add(Integer.toString(le.getLabel()) + " " + Integer.toString(le.getExample()));
        }

        assertTrue(set1.containsAll(set2));
        assertTrue(set2.containsAll(set1));
    }

    private static LabeledDataset<Integer, Integer> newData(Object[][] labelCounts) {
        LabeledDataset<Integer, Integer> result = new LabeledDataset<Integer, Integer>();
        for (int i = 0, k = 1; i < labelCounts.length; i++) {
            int label = (Integer)labelCounts[i][0], count = (Integer)labelCounts[i][1];
            for (int j = 0; j < count; j++) {
                result.add(label, k++);
            }
        }
        return result;
    }
}