package org.latinolib.tokenizer;

import com.google.common.base.Preconditions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * author saxo
 */
public class SimpleTokenizer implements Tokenizer
{
    private final SimpleTokenizerType type;
    private final int minTokenLen;

    public SimpleTokenizer() {
        this.type = SimpleTokenizerType.ALL_CHARS;
        this.minTokenLen = 1;
    }

    public SimpleTokenizer(SimpleTokenizerType type) {
        this.type = Preconditions.checkNotNull(type);
        this.minTokenLen = 1;
    }

    public SimpleTokenizer(SimpleTokenizerType type, int minTokenLen) {
        Preconditions.checkArgument(minTokenLen >= 1);
        this.type = Preconditions.checkNotNull(type);
        this.minTokenLen = minTokenLen;
    }

    public SimpleTokenizerType getType() {
        return type;
    }

    public int getMinTokenLen() {
        return minTokenLen;
    }

    @Override
    public Iterable<Token> getTokens(final String text) {
        return new Iterable<Token>()
        {
            @Override
            public Iterator<Token> iterator() {
                return new TokenIterator(text);
            }
        };
    }

    private class TokenIterator implements Iterator<Token>
    {
        private final CharSequence text;
        private int startIdx = 0;
        private int endIdx = 0;

        public TokenIterator(CharSequence text) {
            this.text = Preconditions.checkNotNull(text);
        }

        @Override
        public boolean hasNext() {
            findNext();
            return startIdx < text.length();
        }

        @Override
        public Token next() {
            findNext();
            if (startIdx < text.length()) {
                Token result = new Token(text.subSequence(startIdx, endIdx).toString(), startIdx, endIdx);
                startIdx = endIdx;
                return result;
            }
            throw new NoSuchElementException();
        }

        private void findNext() {
            do {
                while (startIdx < text.length() && !type.isTokenChar(text.charAt(startIdx))) {
                    startIdx++;
                }
                endIdx = startIdx;
                while (endIdx < text.length() && type.isTokenChar(text.charAt(endIdx))) {
                    endIdx++;
                }
                if (endIdx - startIdx >= minTokenLen) {
                    break;
                }
                startIdx = endIdx;
            }
            while (startIdx < text.length());
        }

        @Override
        public void remove() {
            throw new NotImplementedException();
        }
    }
}
