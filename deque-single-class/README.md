# Deque Single Class

Java 8 POC for a double-ended queue data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/deque/Deque.java
```

## Features

- Add values at the front or back.
- Remove values from the front or back.
- Peek values at both ends without removing them.
- Remove first or last occurrence by value.
- Check `contains`, `size`, and `isEmpty`.
- Convert to array.
- Iterate from front to back or back to front.
- Supports `null` values.

## Example

```java
Deque<String> deque = new Deque<String>();
deque.addFirst("b");
deque.addFirst("a");
deque.addLast("c");

String first = deque.removeFirst();
String last = deque.peekLast();
```

## Test

```bash
mvn test
```
