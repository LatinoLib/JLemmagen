package org.latinolib;

import java.io.Serializable;

/**
 * Author saxo
 */
public interface VectorEntry extends Comparable<VectorEntry>
{
    int getIndex();
    double getData();
    void setData(double value);
}
