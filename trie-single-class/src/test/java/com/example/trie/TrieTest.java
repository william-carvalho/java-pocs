package com.example.trie;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrieTest {
    @Test
    void insertsAndFindsCompleteWords() {
        Trie trie = new Trie();

        assertTrue(trie.insert("cat"));
        assertTrue(trie.insert("car"));

        assertTrue(trie.contains("cat"));
        assertTrue(trie.contains("car"));
        assertFalse(trie.contains("ca"));
        assertFalse(trie.contains("cart"));
        assertEquals(2, trie.size());
    }

    @Test
    void duplicateInsertDoesNotIncreaseSize() {
        Trie trie = new Trie();

        assertTrue(trie.insert("java"));
        assertFalse(trie.insert("java"));

        assertEquals(1, trie.size());
    }

    @Test
    void detectsPrefixes() {
        Trie trie = new Trie();
        trie.insert("flower");
        trie.insert("flow");

        assertTrue(trie.startsWith("flo"));
        assertTrue(trie.startsWith("flower"));
        assertFalse(trie.startsWith("flight"));
    }

    @Test
    void listsWordsByPrefixInSortedOrder() {
        Trie trie = new Trie();
        trie.insert("tea");
        trie.insert("team");
        trie.insert("teach");
        trie.insert("to");
        trie.insert("ten");

        List<String> words = trie.wordsWithPrefix("te");

        assertEquals(Arrays.asList("tea", "teach", "team", "ten"), words);
    }

    @Test
    void returnsEmptyListWhenPrefixIsMissing() {
        Trie trie = new Trie();
        trie.insert("alpha");

        assertTrue(trie.wordsWithPrefix("z").isEmpty());
    }

    @Test
    void deletesLeafWord() {
        Trie trie = new Trie();
        trie.insert("dog");

        assertTrue(trie.delete("dog"));

        assertFalse(trie.contains("dog"));
        assertFalse(trie.startsWith("dog"));
        assertEquals(0, trie.size());
    }

    @Test
    void deletesWordWithoutRemovingSharedPrefix() {
        Trie trie = new Trie();
        trie.insert("car");
        trie.insert("cart");
        trie.insert("carbon");

        assertTrue(trie.delete("cart"));

        assertFalse(trie.contains("cart"));
        assertTrue(trie.contains("car"));
        assertTrue(trie.contains("carbon"));
        assertTrue(trie.startsWith("car"));
        assertEquals(2, trie.size());
    }

    @Test
    void deleteMissingWordReturnsFalse() {
        Trie trie = new Trie();
        trie.insert("house");

        assertFalse(trie.delete("home"));
        assertEquals(1, trie.size());
    }

    @Test
    void findsLongestStoredPrefixOfText() {
        Trie trie = new Trie();
        trie.insert("a");
        trie.insert("app");
        trie.insert("apple");
        trie.insert("application");

        assertEquals("apple", trie.longestPrefixOf("applesauce"));
        assertEquals("application", trie.longestPrefixOf("application-form"));
        assertEquals("", trie.longestPrefixOf("banana"));
    }

    @Test
    void clearRemovesAllWords() {
        Trie trie = new Trie();
        trie.insert("one");
        trie.insert("two");

        trie.clear();

        assertTrue(trie.isEmpty());
        assertEquals(0, trie.size());
        assertFalse(trie.contains("one"));
    }

    @Test
    void validatesInputs() {
        Trie trie = new Trie();

        assertThrows(IllegalArgumentException.class, () -> trie.insert(null));
        assertThrows(IllegalArgumentException.class, () -> trie.insert(""));
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
        assertThrows(IllegalArgumentException.class, () -> trie.startsWith(""));
        assertThrows(IllegalArgumentException.class, () -> trie.wordsWithPrefix(null));
        assertThrows(IllegalArgumentException.class, () -> trie.delete(""));
        assertThrows(IllegalArgumentException.class, () -> trie.longestPrefixOf(null));
    }
}
