package org.latinolib.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author saxo
 */
public class LabeledDataset<T, U>
    extends ArrayList<LabeledExampleEntry<T, U>> implements LabeledExampleCollection<T, U>
{
    private static final long serialVersionUID = -7356318873860736537L;

    public LabeledDataset(int initialCapacity) {
        super(initialCapacity);
    }

    public LabeledDataset() {
    }

    public LabeledDataset(Collection<? extends LabeledExampleEntry<T, U>> c) {
        super(c);
    }

    public void add(T label, U example) {
        super.add(new LabeledExample<T, U>(label, example));
    }
}
