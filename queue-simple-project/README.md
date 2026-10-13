# Simple Queue Project in Java

A queue is a **FIFO** data structure: the first value added is the first value
removed. It works like a customer line—people are served in arrival order.

## Operations

- `enqueue(value)` adds a value at the rear.
- `dequeue()` removes and returns the front value.
- `peek()` reads the front value without removing it.
- `isEmpty()` checks whether the queue has values.
- `size()` returns the number of values.

The linked-node implementation keeps references to both ends of the queue, so
all these operations take **O(1)** time.

## Example problem: customer service

`CustomerService` uses the queue to record customers when they arrive and serve
them in the same order. This is a common queue use case in ticketing, print
jobs, message processing, and task scheduling.

## Run the tests

```bash
mvn test
```

## Run the example

```bash
mvn package
java -cp target/queue-simple-project-1.0-SNAPSHOT.jar com.example.queue.Main
```

Expected output:

```text
Next customer: Ana
Serving: Ana
Serving: Bruno
Customers waiting: 1
```
