package org.latinolib.tokenizer;

/**
 * Author saxo
 */
public interface Tokenizer
{
    Iterable<Token> getTokens(String text);
}
