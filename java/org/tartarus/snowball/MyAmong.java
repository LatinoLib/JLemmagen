package org.tartarus.snowball;

public class MyAmong {
    public MyAmong (String s, int substring_i, int result) {
        this.s_size = s.length();
        this.s = s.toCharArray();
        this.substring_i = substring_i;
	this.result = result;
    }

    public final int s_size; /* search string */
    public final char[] s; /* search string */
    public final int substring_i; /* index to longest matching substring */
    public final int result; /* result of the lookup */
};
