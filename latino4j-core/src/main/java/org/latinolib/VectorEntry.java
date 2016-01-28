package org.latinolib;

import java.io.Serializable;

/**
 * Author saxo
 */
public interface VectorEntry extends Comparable<VectorEntry>, Serializable {
    int getIndex();

    double getData();
    void setData(double value);
}
