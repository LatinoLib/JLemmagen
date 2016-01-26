package org.latinolib;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.latinolib.bow.SparseVector;
import org.latinolib.bow.VectorEntry;

import java.util.Collections;
import java.util.List;

/**
 * Author saxo
 */
public class ModelUtils {
    private ModelUtils() {
    }

    public static SparseVector cutLowWeights(SparseVector vec, double cutLowWgtPerc) {
        Preconditions.checkNotNull(vec);
        Preconditions.checkArgument(cutLowWgtPerc >= 0 && cutLowWgtPerc < 1);
        if (cutLowWgtPerc > 0) {
            double wgtSum = 0;
            List<WeightIndex> tmp = Lists.newArrayListWithCapacity(vec.size());
            for (VectorEntry item : vec) {
                wgtSum += item.getData();
                tmp.add(new WeightIndex(item.getData(), item.getIndex()));
            }
            Collections.sort(tmp);
            double cutSum = cutLowWgtPerc * wgtSum;
            double cutWgt = -1;
            for (WeightIndex item : tmp) {
                cutSum -= item.weight;
                if (cutSum <= 0) {
                    cutWgt = item.weight;
                    break;
                }
            }
            SparseVector newVec = new SparseVector();
            if (cutWgt != -1) {
                for (VectorEntry item : vec) {
                    if (item.getData() >= cutWgt) {
                        newVec.add(item.getIndex(), item.getData());
                    }
                }
            }
            return newVec;
        }
        return vec;
    }

    public static double getVecLenL2(SparseVector vec) {
        Preconditions.checkNotNull(vec);
        double len = 0;
        for (VectorEntry entry : vec) {
            len += entry.getData() * entry.getData();
        }
        return Math.sqrt(len);
    }

    public static boolean tryNrmVecL2(SparseVector vec) {
        double len = getVecLenL2(vec);
        if (len == 0) {
            return false;
        }
        for (VectorEntry entry : vec) {
            entry.setData(entry.getData() / len);
        }
        return true;
    }

    private static class WeightIndex implements Comparable<WeightIndex> {
        public final double weight;
        public final int index;

        private WeightIndex(double weight, int index) {
            this.weight = weight;
            this.index = index;
        }

        @Override
        public int compareTo(WeightIndex o) {
            return Double.compare(weight, o.weight);
        }
    }
}