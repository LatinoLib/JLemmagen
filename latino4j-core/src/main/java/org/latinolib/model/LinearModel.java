package org.latinolib.model;

import com.google.common.base.Preconditions;
import de.bwaldvogel.liblinear.*;
import org.latinolib.SparseVector;
import org.latinolib.VectorEntry;

import java.io.Serializable;

/**
 * Author saxo
 */
public class LinearModel implements Model<Double, SparseVector>, Serializable
{
    private static final long serialVersionUID = -6556601514629098712L;

    private Parameter parameter;
    private de.bwaldvogel.liblinear.Model model;

    public LinearModel(Parameter parameter) {
        this.parameter = Preconditions.checkNotNull(parameter);
    }

    public Parameter getParameter() {
        return parameter;
    }

    public de.bwaldvogel.liblinear.Model getModel() {
        return model;
    }

    @Override
    public void train(LabeledExampleCollection<Double, SparseVector> dataset) {
        Preconditions.checkNotNull(dataset);

        Feature[][] examples = new Feature[dataset.size()][];
        double[] labels = new double[dataset.size()];
        int maxIndex = 0;
        for (int i = 0; i < examples.length; i++) {
            SparseVector labeledExample = dataset.get(i).getExample();
            examples[i] = new Feature[labeledExample.size()];
            int j = 0;
            for (VectorEntry item : labeledExample) {
                examples[i][j++] = new LinearModelFeature(item);
            }
            for (j = 0; j < examples[i].length; j++) {
                if (examples[i][j].getIndex() > maxIndex) {
                    maxIndex = examples[i][j].getIndex();
                }
            }
            labels[i] = dataset.get(i).getLabel();
        }

        Problem problem = new Problem();
        problem.l = examples.length;
        problem.n = maxIndex + 1;
        problem.x = examples;
        problem.y = labels;

        model = Linear.train(problem, parameter);
    }

    @Override
    public Prediction<Double> predict(SparseVector example) {
        Preconditions.checkState(model != null);

        Feature[] vec = new Feature[example.size()];
        int i = 0;
        for (VectorEntry item : example) { vec[i++] = new LinearModelFeature(item); }
        double prediction = Linear.predict(model, vec); // TODO: support for predictProbability
        return new Prediction<Double>(new PredictionScore<Double>(prediction, prediction));
    }
}
