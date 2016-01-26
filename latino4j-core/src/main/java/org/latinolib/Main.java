package org.latinolib;


import no.uib.cipr.matrix.sparse.SparseVector;

public class Main {
    public static void main(String[] args) {
        System.out.println(new PorterStemmer().getStem("friendships"));

        SparseVector v = new SparseVector(1);
    }
}
