package org.latinolib.token;

import java.io.Serializable;

/**
 * Author saxo
 */
public interface Tokenizer extends Serializable {
    Iterable<Token> getTokens(CharSequence text);
}
