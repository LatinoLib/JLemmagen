package org.latinolib.model;

/**
 * Author saxo
 */
public interface Model<T, U>
{
    void train(LabeledExampleCollection<T, U> dataset);
    Prediction<T> predict(U example);
}
