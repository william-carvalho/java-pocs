# Simple Trie Project in Java

A Trie (also called a prefix tree) stores words by sharing their prefixes. For
example, `java` and `javascript` share the same first four nodes.

## The problem

Build a data structure that supports:

- `insert(word)` - adds a word;
- `search(word)` - checks for a complete word;
- `startsWith(prefix)` - checks whether any stored word begins with a prefix.

This implementation accepts lowercase English letters (`a-z`). Each node has
26 possible children and a flag that says whether a complete word ends there.

## Complexity

For a word with `n` letters:

| Operation | Time | Extra space |
|-----------|------|-------------|
| Insert | O(n) | O(n) in the worst case |
| Search | O(n) | O(1) |
| Prefix search | O(n) | O(1) |

## Run the tests

```bash
mvn test
```

## Run the example

```bash
mvn package
java -cp target/trie-simple-project-1.0-SNAPSHOT.jar com.example.trie.Main
```

Expected output:

```text
Contains 'java': true
Contains 'jav': false
Has prefix 'jav': true
Has prefix 'py': false
```
