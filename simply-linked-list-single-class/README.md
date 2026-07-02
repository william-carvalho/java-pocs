# Simply Linked List Single Class

Java 8 POC for a simply linked list data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/simplylinkedlist/SimplyLinkedList.java
```

## Features

- Add values at the beginning or end.
- Insert, read, update, and remove by index.
- Remove by value.
- Check `contains`, `indexOf`, `size`, and `isEmpty`.
- Reverse the list in place.
- Convert to array.
- Iterate from head to tail.
- Supports `null` values.

## Example

```java
SimplyLinkedList<String> list = new SimplyLinkedList<String>();
list.addLast("a");
list.addLast("b");
list.addFirst("start");

list.set(1, "updated");
Object[] values = list.toArray();
```

## Test

```bash
mvn test
```
