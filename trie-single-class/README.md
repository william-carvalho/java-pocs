# Trie Single Class

Java 8 POC for a trie data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/trie/Trie.java
```

## Features

- Insert words.
- Search complete words.
- Check whether a prefix exists.
- Delete words while preserving shared prefixes.
- List words by prefix in sorted order.
- Find the longest stored prefix of a text.
- Clear the trie and inspect size.

## Example

```java
Trie trie = new Trie();
trie.insert("car");
trie.insert("cart");

boolean exists = trie.contains("car");
boolean prefix = trie.startsWith("ca");
List<String> suggestions = trie.wordsWithPrefix("car");
```

## Test

```bash
mvn test
```
