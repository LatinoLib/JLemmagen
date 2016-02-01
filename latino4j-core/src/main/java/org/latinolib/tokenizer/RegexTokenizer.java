package org.latinolib.tokenizer;

import com.google.common.base.Preconditions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author saxo
 */
public class RegexTokenizer implements Tokenizer
{
    private final Pattern pattern;

    public RegexTokenizer() {
        pattern = RegexTokenizers.LATIN.getPattern();
    }

    public RegexTokenizer(Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern);
    }

    public RegexTokenizer(String regex) {
        pattern = Pattern.compile(Preconditions.checkNotNull(regex));
    }

    @Override
    public Iterable<Token> getTokens(final String text) {
        return new Iterable<Token>()
        {
            @Override
            public Iterator<Token> iterator() {
                return new TokenIterator(pattern.matcher(text));
            }
        };
    }

    private class TokenIterator implements Iterator<Token>
    {
        private final Matcher matcher;
        private int startIdx = 0;

        public TokenIterator(Matcher matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean hasNext() {
            boolean result = matcher.find(startIdx);
            if (result) {
                startIdx = matcher.start();
            }
            return result;
        }

        @Override
        public Token next() {
            if (matcher.find(startIdx)) {
                startIdx = matcher.end();
                return new Token(matcher.group(), matcher.start(), matcher.end());
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new NotImplementedException();
        }
    }

}
