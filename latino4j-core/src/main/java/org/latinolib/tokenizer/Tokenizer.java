package org.latinolib.tokenizer;

import java.io.Serializable;

/**
 * Author saxo
 */
public interface Tokenizer extends Serializable
{
    Iterable<Token> getTokens(String text);
}
