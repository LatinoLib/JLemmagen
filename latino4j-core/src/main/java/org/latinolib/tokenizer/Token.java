package org.latinolib.tokenizer;


/**
 * Author saxo
 */
public class Token
{
    private final String text;
    private final int start;
    private final int end;

    public Token(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
