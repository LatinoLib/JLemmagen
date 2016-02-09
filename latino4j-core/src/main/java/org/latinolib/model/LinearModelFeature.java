package org.latinolib.model;

import de.bwaldvogel.liblinear.Feature;
import org.latinolib.VectorEntry;

/**
 * Author mIHA
 */
public class LinearModelFeature implements Feature
{
    private final VectorEntry vecEntry;

    public LinearModelFeature(VectorEntry entry) {
        vecEntry = entry;
    }

    @Override
    public int getIndex() {
        return vecEntry.getIndex();
    }

    @Override
    public double getValue() {
        return vecEntry.getData();
    }

    @Override
    public void setValue(double value) {
        vecEntry.setData(value);
    }
}
