package org.latinolib.tokenizer;

import java.io.Serializable;

/**
 * Author saxo
 */
public interface Tokenizer
{
    Iterable<Token> getTokens(String text);
}
