# Binary Search Single Class

Java 8 POC for the binary search algorithm.

The production code is intentionally in one class:

```text
src/main/java/com/example/binarysearch/BinarySearch.java
```

## Features

- Search sorted `int[]` values.
- Search sorted `List<T extends Comparable<T>>` values.
- Return `-1` when the target is missing.
- Find first and last index for duplicate values.
- Calculate lower bound and upper bound insertion positions.
- Validate null inputs.

## Example

```java
int[] values = new int[]{1, 3, 5, 7};

int index = BinarySearch.search(values, 5);
int insertionPoint = BinarySearch.lowerBound(values, 4);
```

## Test

```bash
mvn test
```
