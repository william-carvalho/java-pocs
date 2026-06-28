# Doubly Linked List Single Class

Java 8 POC for a doubly linked list data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/doublylinkedlist/DoublyLinkedList.java
```

## Features

- Add values at the beginning or end.
- Insert, read, update, and remove by index.
- Remove by value.
- Check `contains`, `indexOf`, `size`, and `isEmpty`.
- Reverse the list in place.
- Convert to forward or reverse arrays.
- Iterate from head to tail or tail to head.
- Supports `null` values.

## Example

```java
DoublyLinkedList<String> list = new DoublyLinkedList<String>();
list.addLast("a");
list.addLast("b");
list.addFirst("start");

list.set(1, "updated");
Object[] values = list.toArray();
Object[] reverse = list.toReverseArray();
```

## Test

```bash
mvn test
```
