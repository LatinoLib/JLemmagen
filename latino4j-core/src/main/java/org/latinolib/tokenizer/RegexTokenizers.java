package org.latinolib.tokenizer;

import java.util.regex.Pattern;

/**
 * Author saxo
 */
public enum RegexTokenizers {
    LATIN {
        private final Pattern pattern = Pattern.compile(
            "[#@$]?([\\d_]*[\\p{InBasicLatin}\\p{InLatin-1Supplement}\\p{InLatinExtended-A}\\p{InLatinExtended-B}\\p{InLatinExtendedAdditional}&&[\\p{L}]][\\d_]*){2,}");

        @Override
        public Pattern getPattern() {
            return pattern;
        }
    },

    LATIN_AND_CYRILLIC {
        private final Pattern pattern = Pattern.compile(
            "[#@$]?([\\d_]*[\\p{InBasicLatin}\\p{InLatin-1Supplement}\\p{InLatinExtended-A}\\p{InLatinExtended-B}\\p{InLatinExtendedAdditional}\\p{InCyrillic}\\p{InCyrillicSupplement}&&[\\p{L}]][\\d_]*){2,}");

        @Override
        public Pattern getPattern() {
            return pattern;
        }
    },

    CYRILLIC {
        private final Pattern pattern = Pattern.compile(
            "[#@$]?([\\d_]*[\\p{InCyrillic}\\p{InCyrillicSupplement}&&[\\p{L}]][\\d_]*){2,}");

        @Override
        public Pattern getPattern() {
            return pattern;
        }
    };

    public abstract Pattern getPattern();

    public RegexTokenizer get() {
        return new RegexTokenizer(getPattern());
    }
}
