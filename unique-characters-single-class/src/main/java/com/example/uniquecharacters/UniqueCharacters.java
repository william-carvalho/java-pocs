package com.example.uniquecharacters;

import java.util.HashSet;
import java.util.Set;

/** Checks whether a string contains only unique Unicode code points. */
public final class UniqueCharacters {

    private UniqueCharacters() {
    }

    /**
     * Uses a set to remember characters already encountered.
     * Time: O(n). Extra space: O(n).
     */
    public static boolean hasAllUniqueCharacters(String value) {
        requireNonNull(value);

        Set<Integer> seen = new HashSet<Integer>();
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            if (!seen.add(codePoint)) {
                return false;
            }
            offset += Character.charCount(codePoint);
        }
        return true;
    }

    /**
     * Compares each character with every character after it and allocates no
     * data structure whose size depends on the input.
     * Time: O(n^2). Extra space: O(1).
     */
    public static boolean hasAllUniqueCharactersWithoutDataStructure(String value) {
        requireNonNull(value);

        for (int firstOffset = 0; firstOffset < value.length();) {
            int first = value.codePointAt(firstOffset);
            int secondOffset = firstOffset + Character.charCount(first);

            while (secondOffset < value.length()) {
                int second = value.codePointAt(secondOffset);
                if (first == second) {
                    return false;
                }
                secondOffset += Character.charCount(second);
            }

            firstOffset += Character.charCount(first);
        }
        return true;
    }

    private static void requireNonNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
    }
}
