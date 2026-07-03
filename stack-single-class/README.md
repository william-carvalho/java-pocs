# Stack Single Class

Java 8 POC for a LIFO stack data structure.

The production code is intentionally in one class:

```text
src/main/java/com/example/stack/Stack.java
```

## Features

- Push values onto the top.
- Pop values from the top.
- Peek the top value without removing it.
- Search from top to bottom.
- Check `contains`, `size`, and `isEmpty`.
- Convert to array from top to bottom.
- Clear and reuse the stack.
- Iterate from top to bottom.
- Supports `null` values.

## Example

```java
Stack<String> stack = new Stack<String>();
stack.push("a");
stack.push("b");

String top = stack.peek();
String removed = stack.pop();
```

## Test

```bash
mvn test
```
