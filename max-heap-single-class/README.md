# Max Heap Single Class

Java 8 POC for a max heap data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/maxheap/MaxHeap.java
```

## Features

- Insert comparable values.
- Peek and extract the maximum value.
- Build a heap from an existing collection.
- Replace the maximum value.
- Remove a specific value.
- Check `contains`, `size`, and `isEmpty`.
- Return a defensive heap-array copy.
- Return values sorted descending without mutating the heap.
- Validate heap ordering.

## Example

```java
MaxHeap<Integer> heap = new MaxHeap<Integer>();
heap.insert(10);
heap.insert(30);
heap.insert(20);

Integer max = heap.extractMax();
List<Integer> sorted = heap.toSortedDescendingList();
```

## Test

```bash
mvn test
```
