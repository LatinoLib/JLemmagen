package org.latinolib.model.linearsvm;

import com.google.common.collect.Lists;
import de.bwaldvogel.liblinear.FeatureNode;
import org.latinolib.bow.VectorEntry;

import java.util.List;

/**
 * Author saxo
 */
public class FeatureEntry extends FeatureNode implements VectorEntry {
    private static final long serialVersionUID = -6131690993299851888L;

    public FeatureEntry(int index, double value) {
        super(index, value);
    }

    @Override
    public double getData() {
        return getValue();
    }

    @Override
    public void setData(double value) {
        setValue(value);
    }

    @Override
    public int compareTo(VectorEntry o) {
        return Integer.compare(index, o.getIndex());
    }

    public static List<FeatureEntry> newEntries(Object[][] indexDataPairs) {
        List<FeatureEntry> result = Lists.newArrayList();
        for (Object[] pair : indexDataPairs) {
            int index = (Integer)pair[0];
            double data = (Double)pair[1];
            result.add(new FeatureEntry(index, data));
        }
        return result;
    }
}
