# Queue Single Class

Java 8 POC for a FIFO queue data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/queue/Queue.java
```

## Features

- Enqueue values at the back.
- Dequeue values from the front.
- Peek the front value without removing it.
- Inspect front and back values.
- Check `contains`, `size`, and `isEmpty`.
- Convert to array.
- Clear and reuse the queue.
- Iterate in queue order.
- Supports `null` values.

## Example

```java
Queue<String> queue = new Queue<String>();
queue.enqueue("a");
queue.enqueue("b");

String front = queue.peek();
String removed = queue.dequeue();
```

## Test

```bash
mvn test
```
