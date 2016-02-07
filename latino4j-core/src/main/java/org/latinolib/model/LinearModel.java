package org.latinolib.model;

import com.google.common.base.Preconditions;
import de.bwaldvogel.liblinear.*;
import org.latinolib.SparseVector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Author saxo
 */
public class LinearModel implements Model<Double, SparseVector>
{
    private static final long serialVersionUID = -6556601514629098712L;

    private transient Parameter parameter;
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
            examples[i] = dataset.get(i).getExample().toArray(new Feature[0]);
            for (int j = 0; j < examples[i].length; j++) {
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

        double prediction = Linear.predict(model, example.toArray(new Feature[0])); // TODO: support for predictProbability
        return new Prediction<Double>(new PredictionScore<Double>(prediction, prediction));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(parameter.getSolverType().toString());
        out.writeDouble(parameter.getC());
        out.writeDouble(parameter.getEps());
        out.writeInt(parameter.getMaxIters());
        out.writeDouble(parameter.getP());
        int numWeights = parameter.getNumWeights();
        out.writeInt(numWeights);
        for (int i = 0; i < numWeights; i++) {
            out.writeDouble(parameter.getWeights()[i]);
            out.writeInt(parameter.getWeightLabels()[i]);
        }
    }

    private void readObject(ObjectInputStream in)
        throws ClassNotFoundException, IOException {
        in.defaultReadObject();

        SolverType solverType = SolverType.valueOf(in.readUTF());
        double c = in.readDouble();
        double eps = in.readDouble();
        int maxIters = in.readInt();
        double p = in.readDouble();
        parameter = new Parameter(solverType, c, eps, maxIters, p);
        int numWeights = in.readInt();
        double[] weights = new double[numWeights];
        int[] weightLabels = new int[numWeights];
        for (int i = 0; i < numWeights; i++) {
            weights[i] = in.readDouble();
            weightLabels[i] = in.readInt();
        }
        parameter.setWeights(weights, weightLabels);
    }
}
