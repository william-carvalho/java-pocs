# Simple Vector Project in Java

This project implements a vector as a **dynamic array**. Unlike a normal array,
the vector grows automatically when it becomes full. Java provides similar
structures through `ArrayList` and the synchronized legacy `Vector` class.

## Operations

- `add(value)` appends a value.
- `get(index)` reads a value.
- `set(index, value)` replaces a value.
- `remove(index)` removes a value and shifts later values left.
- `contains(value)` searches for a value.
- `size()` and `isEmpty()` describe the current contents.

## How growth works

The internal array starts with four positions. When it becomes full, the vector
creates an array with twice the capacity and copies the values into it.

| Operation | Time complexity |
|-----------|-----------------|
| Get or set | O(1) |
| Add | O(1) amortized |
| Search | O(n) |
| Remove | O(n) |

`StudentGrades` demonstrates a practical use by storing a variable number of
grades and calculating their average.

## Run the tests

```bash
mvn test
```

## Run the example

```bash
mvn package
java -cp target/vector-simple-project-1.0-SNAPSHOT.jar com.example.vector.Main
```

Expected output:

```text
First language: Java
Vector size: 3
Contains Java: true
Grade average: 8.0
```
