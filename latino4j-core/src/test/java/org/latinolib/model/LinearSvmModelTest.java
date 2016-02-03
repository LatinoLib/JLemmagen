package org.latinolib.model;

import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.SolverType;

import org.junit.Test;
import org.latinolib.SparseVector;
//import org.latinolib.VectorEntry;

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
 * Author mIHA
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
                    //vec.add(feature, weight);
                    vec.add(new FeatureEntry(feature, weight));
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
        reader.close();
//        for (LabeledExampleEntry<Double, SparseVector> le : ds) {
//            System.out.print(le.getLabel() + " ");
//            for (VectorEntry item : le.getExample()) {
//                System.out.print(item.getIndex() + ":" + item.getData() + " ");
//            }
//            System.out.println();
//        }
        LinearSvmModel model = new LinearSvmModel(new Parameter(SolverType.L1R_L2LOSS_SVC, 1.0, 0.01));
        model.train(ds); // WARNME: this should work with standard sparse vectors not just vectors that contain FeatureEntries
        is = LinearSvmModelTest.class.getResourceAsStream("inductive/test.dat");
        reader = new BufferedReader(new InputStreamReader(is));
        ds = readDataset(reader);
        reader.close();
        int correct = 0;
        for (LabeledExampleEntry<Double, SparseVector> le : ds) { // WARNME: why is this not simply LabeledExample?
            Prediction<Double> p = model.predict(le.getExample());
            Double bestLabel = p.getBest().getLabel();
            if (bestLabel.equals(le.getLabel())) { correct++; }
        }
        double accuracy = (double)correct / (double)ds.size();
        assertTrue(accuracy >= 0.97 && accuracy <= 0.98);
    }

    @Test
    public void testRegression() throws IOException, ParseException {
        InputStream is = LinearSvmModelTest.class.getResourceAsStream("regression/train.dat");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        LabeledDataset<Double, SparseVector> ds = readDataset(reader);
        reader.close();
        LinearSvmModel model = new LinearSvmModel(new Parameter(SolverType.L2R_L2LOSS_SVR, 1.0, 0.01));
        model.train(ds);
        is = LinearSvmModelTest.class.getResourceAsStream("regression/test.dat");
        reader = new BufferedReader(new InputStreamReader(is));
        ds = readDataset(reader);
        reader.close();
        double mae = 0;
        for (LabeledExampleEntry<Double, SparseVector> le : ds) {
            Prediction<Double> p = model.predict(le.getExample());
            Double value = p.getBest().getLabel();
            mae += Math.abs(value - le.getLabel());
        }
        mae /= (double)ds.size();
        assertTrue(mae <= 40.0 && mae >= 30.0);
    }
}