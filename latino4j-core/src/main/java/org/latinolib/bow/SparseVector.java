package org.latinolib.bow;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Author saxo
 */
public class SparseVector implements Collection<VectorEntry> {
    private final List<VectorEntry> entries;

    public SparseVector() {
        entries = Lists.newArrayList();
    }

    public SparseVector(int capacity) {
        entries = Lists.newArrayListWithCapacity(capacity);
    }

    public void add(int index, double data) {
        entries.add(new Entry(index, data));
    }

    public void sort() {
        Collections.sort(entries);
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(entries, (VectorEntry)o) >= 0;
    }

    @Override
    public Iterator<VectorEntry> iterator() {
        return entries.iterator();
    }

    @Override
    public Object[] toArray() {
        return entries.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) entries.toArray();
    }

    public boolean add(VectorEntry entry) {
        return entries.add(entry);
    }

    @Override
    public boolean remove(Object o) {
        return entries.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return entries.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends VectorEntry> c) {
        return entries.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return entries.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return entries.retainAll(c);
    }

    @Override
    public void clear() {
        entries.clear();
    }

    public void add(Collection<VectorEntry> entries) {
        this.entries.addAll(entries);
    }

    public VectorEntry get(int index) {
        return entries.get(index);
    }

    public class Entry implements VectorEntry {
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
    }
}
