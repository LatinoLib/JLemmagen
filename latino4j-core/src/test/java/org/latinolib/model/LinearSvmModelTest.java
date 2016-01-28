package org.latinolib.model;

import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.SolverType;
import org.junit.Test;
import org.latinolib.SparseVector;
import org.latinolib.model.*;

import static org.junit.Assert.*;

/**
 * Author saxo
 */
public class LinearSvmModelTest {

    @Test
    public void testTrain() throws Exception {

        LabeledDataset<Double, SparseVector> data = new LabeledDataset<Double, SparseVector>();
        data.add(new LabeledExample<Double, SparseVector>(1d, new SparseVector(FeatureEntry
                .newEntries(new Object[][]{ {2,0.1}, {3,0.2}, {6,1.0} }))));
        data.add(new LabeledExample<Double, SparseVector>(2d, new SparseVector(FeatureEntry
                .newEntries(new Object[][]{ {2,0.1}, {3,0.3}, {4,-1.2}, {6,1.0} }))));
        data.add(new LabeledExample<Double, SparseVector>(1d, new SparseVector(FeatureEntry
                .newEntries(new Object[][]{ {1,0.4}, {6,1.0} }))));
        data.add(new LabeledExample<Double, SparseVector>(2d, new SparseVector(FeatureEntry
                .newEntries(new Object[][]{ {2,0.1}, {4,1.4}, {5,0.5}, {6,1.0} }))));
        data.add(new LabeledExample<Double, SparseVector>(3d, new SparseVector(FeatureEntry
                .newEntries(new Object[][]{ {1,-0.1}, {2,-0.2}, {3,0.1}, {4,1.1}, {5,0.1}, {6,1.0} }))));

        Parameter parameter = new Parameter(SolverType.L1R_L2LOSS_SVC, 1, 0.001);
        LinearSvmModel model = new LinearSvmModel(parameter);

        model.train(data);

        Prediction<Double> prediction = model.predict(new SparseVector(FeatureEntry
                .newEntries(new Object[][]{{1, -0.1}, {2, -0.2}, {3, 0.1}, {4, 1.1}, {5, 0.1}, {6, 1.0}})));

        assertEquals(2d, prediction.getBest().getScore(), 0);
    }

    @Test
    public void testPredict() throws Exception {

    }
}