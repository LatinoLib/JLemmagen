package org.latinolib.model;

import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.SolverType;

import org.junit.Test;
import org.latinolib.SparseVector;
import org.latinolib.VectorEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Author saxo, mIHA
 */
public class LinearSvmModelTest
{
    private static LabeledDataset<Double, SparseVector> readDataset(BufferedReader reader) throws IOException, ParseException {
        String line;
        LabeledDataset<Double, SparseVector> ds = new LabeledDataset<Double, SparseVector>();
        while ((line = reader.readLine()) != null) {
            SparseVector vec = new SparseVector();
            Pattern labelPattern = Pattern.compile("^(?<label>[+-]?\\d+([.]\\d+)?)(\\s|$)");
            Pattern featurePattern = Pattern.compile("(?i)(?<feature>(\\d+|qid)):(?<weight>[-]?[\\d\\.]+)");
            if (!line.startsWith("#")) {
                Matcher labelMatch = labelPattern.matcher(line);
                labelMatch.find();
                double label = Double.parseDouble(labelMatch.group("label"));
                Matcher match = featurePattern.matcher(line);
                while (match.find()) {
                    int feature = Integer.parseInt(match.group("feature"));
                    double weight = NumberFormat.getInstance(Locale.US).parse(match.group("weight")).doubleValue();
                    vec.add(feature, weight);
                }
                ds.add(label, vec);
            }
        }
        return ds;
    }

    @Test
    public void testInduction() throws IOException, ParseException {
        InputStream is = LinearSvmModelTest.class.getResourceAsStream("inductive/train.dat");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        LabeledDataset<Double, SparseVector> ds = readDataset(reader);
        // debug code
        for (LabeledExampleEntry<Double, SparseVector> le : ds) {
            System.out.print(le.getLabel() + " ");
            for (VectorEntry item : le.getExample()) {
                System.out.print(item.getIndex() + ":" + item.getData() + " ");
            }
            System.out.println();
        }
        // end of debug code
        // ...
        reader.close();
    }

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