package edu.isistan.spellchecker.lsh;

import java.util.HashSet;
import java.util.Set;

public class NGram {
    public static Set<String> ngrams(int n, String word) {
        Set<String> ngrams = new HashSet<>();
        if (n == 1) {
            for (int i = 0; i < word.length(); i++) {
                ngrams.add(String.valueOf(word.charAt(i)));
            }
        } else {
            for (int i = 0; i < word.length() - n + 1; i++) {
                ngrams.add(word.substring(i, i + n));
            }
        }
        return ngrams;
    }
}
