package org.latinolib.bow;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author saxo
 */
public class Word implements Iterable<Map.Entry<String, Integer>>, Serializable
{
    private static final long serialVersionUID = 812213181817310413L;

    int idx = -1;
    final Map<String, Integer> forms = new HashMap<String, Integer>();
    String mostFrequentForm;
    String stem;
    int docFreq = 1;
    int freq = 1;
    double idf = -1;

    public Word(String word, String stem) {
        this.stem = Preconditions.checkNotNull(stem);
        mostFrequentForm = Preconditions.checkNotNull(word);
        forms.put(word, 1);
    }

    public String getMostFrequentForm() {
        return mostFrequentForm;
    }

    public String getStem() {
        return stem;
    }

    public int getDocFreq() {
        return docFreq;
    }

    public void incDocFreq() {
        this.docFreq += 1;
    }

    public int getFreq() {
        return freq;
    }

    public void incFreq() {
        this.freq += 1;
    }

    public double getIdf() {
        return idf;
    }

    public Iterable<String> getWordForms() {
        return forms.keySet();
    }

    public static long getHashCode64(String word)
    {
        return Hashing.sha1().hashString(Preconditions.checkNotNull(word)).asLong();
    }

    public long hashCode64() {
        return getHashCode64(stem);
    }

    @Override
    public Iterator<Map.Entry<String, Integer>> iterator() {
        return forms.entrySet().iterator();
    }
}
