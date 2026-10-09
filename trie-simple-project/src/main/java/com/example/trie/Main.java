package com.example.trie;

public class Main {
    public static void main(String[] args) {
        Trie dictionary = new Trie();
        dictionary.insert("java");
        dictionary.insert("javascript");
        dictionary.insert("job");

        System.out.println("Contains 'java': " + dictionary.search("java"));
        System.out.println("Contains 'jav': " + dictionary.search("jav"));
        System.out.println("Has prefix 'jav': " + dictionary.startsWith("jav"));
        System.out.println("Has prefix 'py': " + dictionary.startsWith("py"));
    }
}
