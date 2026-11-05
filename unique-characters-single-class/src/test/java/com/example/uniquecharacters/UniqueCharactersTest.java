package com.example.uniquecharacters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UniqueCharactersTest {

    @Test
    void detectsUniqueCharacters() {
        assertBothMethodsReturnTrue("");
        assertBothMethodsReturnTrue("a");
        assertBothMethodsReturnTrue("abcdef");
        assertBothMethodsReturnTrue("Aa");
        assertBothMethodsReturnTrue("a b");
    }

    @Test
    void detectsDuplicateCharacters() {
        assertBothMethodsReturnFalse("hello");
        assertBothMethodsReturnFalse("aba");
        assertBothMethodsReturnFalse("two words");
    }

    @Test
    void treatsSupplementaryUnicodeCodePointsAsCharacters() {
        assertBothMethodsReturnTrue("a\uD83D\uDE00b\uD83D\uDE03");
        assertBothMethodsReturnFalse("a\uD83D\uDE00b\uD83D\uDE00");
    }

    @Test
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> UniqueCharacters.hasAllUniqueCharacters(null));
        assertThrows(IllegalArgumentException.class,
                () -> UniqueCharacters.hasAllUniqueCharactersWithoutDataStructure(null));
    }

    private static void assertBothMethodsReturnTrue(String value) {
        assertTrue(UniqueCharacters.hasAllUniqueCharacters(value));
        assertTrue(UniqueCharacters.hasAllUniqueCharactersWithoutDataStructure(value));
    }

    private static void assertBothMethodsReturnFalse(String value) {
        assertFalse(UniqueCharacters.hasAllUniqueCharacters(value));
        assertFalse(UniqueCharacters.hasAllUniqueCharactersWithoutDataStructure(value));
    }
}
