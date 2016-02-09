package org.latinolib;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Author saxo
 */
public class SparseVector implements Iterable<VectorEntry>
{
    private final List<VectorEntry> entries;

    public SparseVector() {
        entries = Lists.newArrayList();
    }

    public SparseVector(int capacity) {
        entries = Lists.newArrayListWithCapacity(capacity);
    }

    public SparseVector(Collection<? extends VectorEntry> entries) {
        this.entries = Lists.newArrayList(entries);
    }

    public SparseVector(VectorEntry... entries) {
        this.entries = Lists.newArrayList(entries);
    }

    public static SparseVector sorted(Collection<? extends VectorEntry> entries) {
        SparseVector vec = new SparseVector(entries);
        vec.sort();
        return vec;
    }

    public static SparseVector sorted(VectorEntry... entries) {
        SparseVector vec = new SparseVector(entries);
        vec.sort();
        return vec;
    }

    public List<VectorEntry> innerEntries() {
        return entries;
    }

    public int innerIndexOf(VectorEntry entry) {
        return Collections.binarySearch(entries, entry);
    }

    public int innerIndexOf(int entryIndex) {
        return innerIndexOf(new Entry(entryIndex, 0));
    }

    public void sort() {
        Collections.sort(entries);
    }

    public int size() {
        return entries.size();
    }

    public VectorEntry get(int index) {
        int innerIndex = innerIndexOf(index);
        return innerIndex >= 0 ? entries.get(innerIndex) : null;
    }

    public void add(VectorEntry entry) {
        entries.add(Preconditions.checkNotNull(entry));
    }

    public void add(int index, double data) {
        entries.add(new Entry(index, data));
    }

    public boolean contains(int index) {
        return innerIndexOf(index) >= 0;
    }

    public Object[] toArray() {
        return entries.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return entries.toArray(a);
    }

    @Override
    public Iterator<VectorEntry> iterator() {
        final Iterator<VectorEntry> iter = entries.iterator();
        return new Iterator<VectorEntry>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public VectorEntry next() {
                return iter.next();
            }

            @Override
            public void remove() {
                throw new NotImplementedException();
            }
        };
    }

    public class Entry implements VectorEntry
    {
        private static final long serialVersionUID = -3612083403200155767L;
        private int index;
        private double data;

        public Entry(int index, double data) {
            this.index = index;
            this.data = data;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public double getData() {
            return data;
        }

        @Override
        public void setData(double value) {
            this.data = value;
        }

        @Override
        public int compareTo(VectorEntry o) {
            return Integer.compare(index, o.getIndex());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            Entry entry = (Entry)o;
            return index == entry.index && data == entry.data;
        }

        @Override
        public int hashCode() {
            long temp = Double.doubleToLongBits(data);
            return 31 * index + (int)(temp ^ (temp >>> 32));
        }
    }
}
