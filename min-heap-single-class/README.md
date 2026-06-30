# Min Heap Single Class

Java 8 POC for a min heap data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/minheap/MinHeap.java
```

## Features

- Insert comparable values.
- Peek and extract the minimum value.
- Build a heap from an existing collection.
- Replace the minimum value.
- Remove a specific value.
- Check `contains`, `size`, and `isEmpty`.
- Return a defensive heap-array copy.
- Return values sorted ascending without mutating the heap.
- Validate heap ordering.

## Example

```java
MinHeap<Integer> heap = new MinHeap<Integer>();
heap.insert(10);
heap.insert(3);
heap.insert(20);

Integer min = heap.extractMin();
List<Integer> sorted = heap.toSortedAscendingList();
```

## Test

```bash
mvn test
```
