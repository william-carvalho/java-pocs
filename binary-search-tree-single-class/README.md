# Binary Search Tree Single Class

Java 8 POC for a binary search tree data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/bst/BinarySearchTree.java
```

## Features

- Insert comparable values.
- Search values.
- Reject duplicate values.
- Remove leaf nodes, nodes with one child, and nodes with two children.
- Return min, max, height, size, and empty state.
- Traverse in order, pre order, and post order.
- Clear the tree.

## Example

```java
BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();
tree.insert(8);
tree.insert(3);
tree.insert(10);

boolean exists = tree.contains(3);
List<Integer> sorted = tree.inOrder();
tree.remove(8);
```

## Test

```bash
mvn test
```
