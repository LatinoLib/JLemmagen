package org.latinolib.model;

/**
 * Author saxo
 */
public interface LabeledExampleEntry<T, U> {
    T getLabel();

    U getExample();
}
