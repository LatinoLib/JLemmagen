package org.latinolib.model;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.latinolib.Language;
import org.latinolib.SparseVector;
import org.latinolib.bow.BowSpace;
import org.latinolib.bow.WordWeightType;
import org.latinolib.eval.CrossValidator;
import org.latinolib.eval.PerfData;
import org.latinolib.eval.PerfMetric;
import org.latinolib.tokenizer.RegexTokenizers;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author saxo
 */
public class TwoPlaneClassifierTest
{
    @Test
    public void testAccuracy() throws IOException {
        List<LabeledExample<String, String>> labeledExamples = getLabeledExamples();
        LabeledDataset<String, SparseVector> dataset = getBowVectors(labeledExamples);

        PerfData<String> perfData = CrossValidator.stratified(10, dataset).runModel(
            CrossValidator.getModelList(new Supplier<Model<String, SparseVector>>()
            {
                @Override
                public Model<String, SparseVector> get() {
                    return new TwoPlaneClassifier<String>("Negative", "Neutral", "Positive");
                }
            }, 10));

        double accuracy = perfData.getAvg("", "", PerfMetric.ACCURACY);
        assertTrue(accuracy >= 0.5 && accuracy <= 0.7);
    }

    @Test
    public void testAccuracyMultiThreaded() throws IOException, ExecutionException, InterruptedException {
        List<LabeledExample<String, String>> labeledExamples = getLabeledExamples();
        LabeledDataset<String, SparseVector> dataset = getBowVectors(labeledExamples);

        PerfData<String> perfData = CrossValidator
            .stratified(10, dataset)
            .runModel(CrossValidator.getModelList(new Supplier<Model<String, SparseVector>>()
            {
                @Override
                public Model<String, SparseVector> get() {
                    return new TwoPlaneClassifier<String>("Negative", "Neutral", "Positive");
                }
            }, 10), Executors.newFixedThreadPool(15));

        double accuracy = perfData.getAvg("", "", PerfMetric.ACCURACY);
        assertTrue(accuracy >= 0.5 && accuracy <= 0.7);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, ParseException {
        List<LabeledExample<String, String>> labeledExamples = getLabeledExamples();
        LabeledDataset<String, SparseVector> dataset = getBowVectors(labeledExamples);

        TwoPlaneClassifier<String> classifier = new TwoPlaneClassifier<String>("Negative", "Neutral", "Positive");
        PerfData<String> perfData = CrossValidator.stratified(10, dataset, false, null).runModel(
            CrossValidator.getModelList(new Supplier<Model<String, SparseVector>>()
            {
                @Override
                public Model<String, SparseVector> get() {
                    return new TwoPlaneClassifier<String>("Negative", "Neutral", "Positive");
                }
            }, 10));
        double expectedAccuracy = perfData.getAvg("", "", PerfMetric.ACCURACY);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(classifier);

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream oin = new ObjectInputStream(bin);

        perfData = CrossValidator.stratified(10, dataset, false, null).runModel(
            CrossValidator.getModelList(new Supplier<Model<String, SparseVector>>()
            {
                @Override
                public Model<String, SparseVector> get() {
                    return new TwoPlaneClassifier<String>("Negative", "Neutral", "Positive");
                }
            }, 10));
        double accuracy = perfData.getAvg("", "", PerfMetric.ACCURACY);

        assertEquals(expectedAccuracy, accuracy, 0.01);
    }

    private LabeledDataset<String, SparseVector> getBowVectors(List<LabeledExample<String, String>> labeledExamples)
            throws IOException {
        BowSpace bow = new BowSpace();
        bow.setTokenizer(RegexTokenizers.LATIN.get());
        bow.setStopWords(null);
        bow.setStemmer(Language.EN.getLemmatizer());
        bow.setMaxNGramLen(2);
        bow.setMinWordFreq(5);
        bow.setWordWeightType(WordWeightType.TF_IDF);
        bow.setNormalizeVectors(true);
        bow.setKeepWordForms(false);
        List<String> texts = Lists.newArrayList();
        for (LabeledExample<String, String> le : labeledExamples) {
            texts.add(le.getExample());
        }
        bow.initialize(texts, true, false);
        LabeledDataset<String, SparseVector> dataset = new LabeledDataset<String, SparseVector>();
        for (LabeledExample<String, String> le : labeledExamples) {
            SparseVector vector = bow.processDocument(le.getExample(), true);
            dataset.add(le.getLabel(), vector);
        }
        return dataset;
    }

    private List<LabeledExample<String, String>> getLabeledExamples() throws IOException {
        Pattern urlRegex = Pattern.compile("http\\S*", Pattern.CASE_INSENSITIVE);
        Pattern stockRefRegex = Pattern.compile("\\$\\w+", Pattern.CASE_INSENSITIVE);
        Pattern userRefRegex = Pattern.compile("@\\w+", Pattern.CASE_INSENSITIVE);
        Pattern letterRepetitionRegex = Pattern.compile("(.)\\1{2,}", Pattern.CASE_INSENSITIVE);

        InputStream is = TwoPlaneClassifierTest.class.getResourceAsStream("twoplane\\stanford_manual_2009_06_14.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<LabeledExample<String, String>> examples = Lists.newArrayList();
        Map<String, Integer> fieldNames = null;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (fieldNames == null) {
                fieldNames = new HashMap<String, Integer>();
                String[] names = line.split("\t");
                for (int i = 0; i < names.length; i++) {
                    fieldNames.put(names[i], i);
                }
                continue;
            }
            String[] fields = line.split("\t");

            String text = fields[fieldNames.get("Text")];
            text = urlRegex.matcher(text).replaceAll(""); // rmv URLs
            text = stockRefRegex.matcher(text).replaceAll(""); // rmv stock refs
            text = userRefRegex.matcher(text).replaceAll(""); // rmv user refs
            text = letterRepetitionRegex.matcher(text).replaceAll("$1$1"); // collapse letter repetitions
            text = text.replaceAll("'", ""); // rmv apos
            text = text.toLowerCase().replace("^rt\\W", ""); // remove RT

            examples.add(new LabeledExample<String, String>(
                fields[fieldNames.get("Label")],
                text));
        }
        return examples;
    }
}