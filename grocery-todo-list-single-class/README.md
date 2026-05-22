# Grocery TODO List Single Class

Java 8 POC for a grocery TODO list with add, remove, done, do, redo, and listAll operations.

The production code is intentionally in one class:

```text
src/main/java/com/example/grocerytodo/GroceryTodoList.java
```

## Rules

- `addItem` creates a `PENDING` grocery item.
- `removeItem` removes an item by id.
- `markAsDone` changes the item status to `DONE`.
- `doItem` changes the item status to `PENDING`.
- `redoItem` also changes the item status to `PENDING`.
- `listAll` returns all current items in insertion order.
- `listByStatus` can filter pending or done items.

## Example

```java
GroceryTodoList list = new GroceryTodoList();

GroceryTodoList.GroceryItem milk = list.addItem("Milk", "2 liters");
list.markAsDone(milk.getId());
list.redoItem(milk.getId());

List<GroceryTodoList.GroceryItem> items = list.listAll();
```

## Test

```bash
mvn test
```
