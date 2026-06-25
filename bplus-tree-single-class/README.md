# B+ Tree Single Class

Java 8 POC for a B+ tree data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/bplustree/BPlusTree.java
```

## Features

- Insert and update key/value pairs.
- Search values by key.
- Check whether a key exists, including keys mapped to `null`.
- Delete keys.
- Return all keys in sorted order.
- Range search through linked leaf nodes.
- Return first key, last key, size, height, and empty state.
- Clear the tree.

## Example

```java
BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>(4);
tree.put(10, "ten");
tree.put(20, "twenty");
tree.put(15, "fifteen");

String value = tree.get(15);
List<String> range = tree.rangeSearch(10, 20);
tree.remove(10);
```

## Test

```bash
mvn test
```
