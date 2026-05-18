# Restaurant Queue Single Class

Java 8 POC for a restaurant queue system that can tell how long each dish and order will take.

The production code is intentionally in one class:

```text
src/main/java/com/example/restaurantqueue/RestaurantQueue.java
```

## Rules

- Each dish has a preparation time in minutes.
- Each order item calculates `dish time * quantity`.
- An order total is the sum of all item preparation times.
- Queue estimates include position, estimated start, and estimated completion.
- `WAITING` and `IN_PROGRESS` orders count in the queue.
- `DONE` and `CANCELLED` orders are ignored in queue estimates.

## Example

```java
RestaurantQueue queue = RestaurantQueue.withDefaultDishes();

RestaurantQueue.Order order = queue.addOrder(
        "Ana",
        RestaurantQueue.item("Burger", 2),
        RestaurantQueue.item("Salad", 1));

RestaurantQueue.OrderEstimate estimate = queue.estimateOrder(order.getId());

int totalMinutes = estimate.getTotalPreparationTimeMinutes(); // 38
int burgerMinutes = estimate.getDishes().get(0).getTotalPreparationTimeMinutes(); // 30
```

## Test

```bash
mvn test
```
