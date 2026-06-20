# Segment Tree Single Class

Java 8 POC for a segment tree data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/segmenttree/SegmentTree.java
```

## Features

- Build from an integer array.
- Range query in `O(log n)`.
- Point update in `O(log n)`.
- Built-in sum, min, max, and gcd trees.
- Custom associative operations through `IntOperation`.

## Example

```java
SegmentTree tree = SegmentTree.sum(new int[]{1, 3, 5, 7});

int total = tree.query(0, 3);
tree.update(1, 10);
int partial = tree.query(1, 2);
```

## Test

```bash
mvn test
```
