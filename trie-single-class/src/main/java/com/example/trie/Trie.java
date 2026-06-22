package com.example.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class Trie {
    private final Node root = new Node();
    private int size;

    public boolean insert(String word) {
        validateWord(word, "word");
        Node current = root;
        for (int index = 0; index < word.length(); index++) {
            char character = word.charAt(index);
            Node next = current.children.get(character);
            if (next == null) {
                next = new Node();
                current.children.put(character, next);
            }
            current = next;
        }

        if (current.word) {
            return false;
        }
        current.word = true;
        size++;
        return true;
    }

    public boolean contains(String word) {
        validateWord(word, "word");
        Node node = findNode(word);
        return node != null && node.word;
    }

    public boolean startsWith(String prefix) {
        validateWord(prefix, "prefix");
        return findNode(prefix) != null;
    }

    public boolean delete(String word) {
        validateWord(word, "word");
        if (!contains(word)) {
            return false;
        }
        delete(root, word, 0);
        size--;
        return true;
    }

    public List<String> wordsWithPrefix(String prefix) {
        validateWord(prefix, "prefix");
        Node start = findNode(prefix);
        if (start == null) {
            return Collections.emptyList();
        }

        List<String> words = new ArrayList<String>();
        collect(start, new StringBuilder(prefix), words);
        return words;
    }

    public String longestPrefixOf(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text is required");
        }

        Node current = root;
        int longestLength = -1;
        for (int index = 0; index < text.length(); index++) {
            current = current.children.get(text.charAt(index));
            if (current == null) {
                break;
            }
            if (current.word) {
                longestLength = index + 1;
            }
        }

        if (longestLength == -1) {
            return "";
        }
        return text.substring(0, longestLength);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root.children.clear();
        root.word = false;
        size = 0;
    }

    private boolean delete(Node current, String word, int index) {
        if (index == word.length()) {
            current.word = false;
            return current.children.isEmpty();
        }

        char character = word.charAt(index);
        Node child = current.children.get(character);
        boolean removeChild = delete(child, word, index + 1);
        if (removeChild) {
            current.children.remove(character);
        }
        return !current.word && current.children.isEmpty();
    }

    private Node findNode(String text) {
        Node current = root;
        for (int index = 0; index < text.length(); index++) {
            current = current.children.get(text.charAt(index));
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private void collect(Node current, StringBuilder prefix, List<String> words) {
        if (current.word) {
            words.add(prefix.toString());
        }

        for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
            prefix.append(entry.getKey().charValue());
            collect(entry.getValue(), prefix, words);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    private void validateWord(String value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
    }

    private static final class Node {
        private final Map<Character, Node> children = new TreeMap<Character, Node>();
        private boolean word;
    }
}
