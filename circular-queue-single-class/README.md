# Circular Queue Single Class

Java 8 POC for a fixed-capacity circular queue data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/circularqueue/CircularQueue.java
```

## Features

- Enqueue values at the back.
- Dequeue values from the front.
- Peek the front value without removing it.
- Reuse freed array positions through circular indexing.
- Report size, capacity, remaining capacity, full state, and empty state.
- Convert queue contents to an ordered array.
- Clear and reuse the queue.
- Supports `null` values.

## Example

```java
CircularQueue<String> queue = new CircularQueue<String>(3);
queue.enqueue("a");
queue.enqueue("b");

String next = queue.dequeue();
queue.enqueue("c");
Object[] values = queue.toArray();
```

## Test

```bash
mvn test
```
