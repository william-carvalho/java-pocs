package com.example.trie;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrieTest {
    @Test
    void insertsAndSearchesWords() {
        Trie trie = new Trie();
        trie.insert("apple");

        assertTrue(trie.search("apple"));
        assertFalse(trie.search("app"));
    }

    @Test
    void recognizesPrefixes() {
        Trie trie = new Trie();
        trie.insert("apple");

        assertTrue(trie.startsWith("app"));
        assertFalse(trie.startsWith("car"));
    }

    @Test
    void acceptsAWordThatWasPreviouslyOnlyAPrefix() {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");

        assertTrue(trie.search("app"));
        assertTrue(trie.search("apple"));
    }

    @Test
    void rejectsInvalidInput() {
        Trie trie = new Trie();

        assertThrows(IllegalArgumentException.class, () -> trie.insert(""));
        assertThrows(IllegalArgumentException.class, () -> trie.insert("Java"));
        assertThrows(IllegalArgumentException.class, () -> trie.search(null));
    }
}
