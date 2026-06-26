# Circular Linked List Single Class

Java 8 POC for a singly circular linked list data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/circularlinkedlist/CircularLinkedList.java
```

## Features

- Add values at the beginning or end.
- Insert, read, and remove by index.
- Remove by value.
- Check `contains`, `indexOf`, `size`, and `isEmpty`.
- Rotate the head around the circular list.
- Confirm circular integrity with `isCircular`.
- Convert to array and iterate for one cycle.
- Supports `null` values.

## Example

```java
CircularLinkedList<String> list = new CircularLinkedList<String>();
list.addLast("a");
list.addLast("b");
list.addLast("c");

list.rotate(1);
Object[] values = list.toArray();
```

## Test

```bash
mvn test
```
