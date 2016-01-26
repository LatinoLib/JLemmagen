package org.latinolib.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Author saxo
 */
public class LabeledDataset<T extends Serializable, U extends Serializable>
        extends ArrayList<LabeledExampleEntry<T, U>> implements LabeledExampleCollection<T, U>, Serializable {
    private static final long serialVersionUID = 1000846716694285541L;

    public LabeledDataset(int initialCapacity) {
        super(initialCapacity);
    }

    public LabeledDataset() {
    }

    public LabeledDataset(Collection<? extends LabeledExampleEntry<T, U>> c) {
        super(c);
    }
}
