package org.latinolib.token;


/**
 * Author saxo
 */
public class Token {
    private final CharSequence text;
    private final int start;
    private final int end;

    public Token(CharSequence text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    public CharSequence getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
