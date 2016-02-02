package org.latinolib.model;

/**
 * Author saxo
 */
public class LabeledExample<T, U> implements LabeledExampleEntry<T, U>
{
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
