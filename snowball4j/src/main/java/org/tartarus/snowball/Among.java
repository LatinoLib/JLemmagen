package org.tartarus.snowball;

public class Among {
    public Among (String s, int substring_i, int result,
		  String methodname) {
        this.s_size = s.length();
        this.s = s.toCharArray();
        this.substring_i = substring_i;
	this.result = result;
	this.methodname = methodname;
    }

    public final int s_size; /* search string */
    public final char[] s; /* search string */
    public final int substring_i; /* index to longest matching substring */
    public final int result; /* result of the lookup */
    public final String methodname;
};
