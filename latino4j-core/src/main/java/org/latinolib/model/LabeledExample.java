package org.latinolib.model;

import java.io.Serializable;

/**
 * Author saxo
 */
public class LabeledExample<T extends Serializable, U extends Serializable>
        implements LabeledExampleEntry<T, U>, Serializable
{
    private static final long serialVersionUID = 820725723590267520L;

    private final T label;
    private final U example;

    public LabeledExample(T label, U example) {
        this.label = label;
        this.example = example;
    }

    @Override
    public T getLabel() {
        return label;
    }

    @Override
    public U getExample() {
        return example;
    }
}
