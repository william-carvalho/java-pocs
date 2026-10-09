package com.example.trie;

/**
 * A Trie stores words one character at a time.
 *
 * Each path from the root represents a prefix. Nodes marked as the end of a
 * word represent complete words.
 */
public class Trie {
    private final Node root = new Node();

    public void insert(String word) {
        validate(word);
        Node current = root;

        for (char letter : word.toCharArray()) {
            int index = toIndex(letter);
            if (current.children[index] == null) {
                current.children[index] = new Node();
            }
            current = current.children[index];
        }

        current.endOfWord = true;
    }

    public boolean search(String word) {
        validate(word);
        Node node = find(word);
        return node != null && node.endOfWord;
    }

    public boolean startsWith(String prefix) {
        validate(prefix);
        return find(prefix) != null;
    }

    private Node find(String text) {
        Node current = root;

        for (char letter : text.toCharArray()) {
            current = current.children[toIndex(letter)];
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private void validate(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text must not be null or empty");
        }
        for (char letter : text.toCharArray()) {
            toIndex(letter);
        }
    }

    private int toIndex(char letter) {
        if (letter < 'a' || letter > 'z') {
            throw new IllegalArgumentException("Only lowercase letters a-z are supported");
        }
        return letter - 'a';
    }

    private static class Node {
        private final Node[] children = new Node[26];
        private boolean endOfWord;
    }
}
