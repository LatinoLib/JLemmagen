package org.latinolib.model;

import java.io.Serializable;

/**
 * Author saxo
 */
public interface Model<T, U> extends Serializable
{
    void train(LabeledExampleCollection<T, U> dataset);
    Prediction<T> predict(U example);
}
