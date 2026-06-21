# Linked List Single Class

Java 8 POC for a singly linked list data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/linkedlist/LinkedList.java
```

## Features

- Add values at the beginning or end.
- Insert, read, and remove by index.
- Remove by value.
- Check `contains`, `indexOf`, `size`, and `isEmpty`.
- Reverse the list in place.
- Convert to array and iterate with `Iterable`.
- Supports `null` values.

## Example

```java
LinkedList<String> list = new LinkedList<String>();
list.addLast("first");
list.addLast("second");
list.insertAt(1, "middle");
list.reverse();
```

## Test

```bash
mvn test
```
