package org.latinolib.tokenizer;

/**
 * author saxo
 */
public enum SimpleTokenizerType {

    ALL_CHARS {
        @Override
        public boolean isTokenChar(char ch) {
            return !Character.isWhitespace(ch);
        }
    },
    ALPHA_ONLY {
        @Override
        public boolean isTokenChar(char ch) {
            return Character.isLetter(ch);
        }
    },
    ALPHANUM_ONLY {
        @Override
        public boolean isTokenChar(char ch) {
            return Character.isLetterOrDigit(ch);
        }
    };

    public abstract boolean isTokenChar(char ch);
}
