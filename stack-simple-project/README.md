# Simple Stack Project in Java

A stack is a **LIFO** data structure: the last value added is the first value
removed. Think of a pile of books—you add and remove books from the top.

## Operations

- `push(value)` adds a value to the top.
- `pop()` removes and returns the top value.
- `peek()` reads the top value without removing it.
- `isEmpty()` checks whether the stack has values.
- `size()` returns the number of values.

All these operations take **O(1)** time in this linked-node implementation.

## Example problem: balanced brackets

`BracketValidator` checks expressions such as `{[2 + (3 * 4)]}`. Opening
brackets are pushed onto the stack. Each closing bracket must match the most
recent opening bracket, which is popped from the stack.

## Run the tests

```bash
mvn test
```

## Run the example

```bash
mvn package
java -cp target/stack-simple-project-1.0-SNAPSHOT.jar com.example.stack.Main
```

Expected output:

```text
Top book: Effective Java
Removed: Effective Java
Stack size: 1
Balanced expression: true
```
