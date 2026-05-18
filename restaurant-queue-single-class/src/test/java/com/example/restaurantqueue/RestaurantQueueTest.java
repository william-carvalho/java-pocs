package com.example.restaurantqueue;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestaurantQueueTest {
    @Test
    void tellsHowLongEachDishWillTakeInsideAnOrder() {
        RestaurantQueue queue = RestaurantQueue.withDefaultDishes();

        RestaurantQueue.Order order = queue.addOrder(
                "Ana",
                RestaurantQueue.item("Burger", 2),
                RestaurantQueue.item("Salad", 1));

        RestaurantQueue.OrderEstimate estimate = queue.estimateOrder(order.getId());

        assertEquals(38, estimate.getTotalPreparationTimeMinutes());
        assertEquals(2, estimate.getDishes().size());
        assertDish(estimate.getDishes().get(0), "Burger", 2, 15, 30);
        assertDish(estimate.getDishes().get(1), "Salad", 1, 8, 8);
    }

    @Test
    void estimatesStartAndCompletionTimesForQueuedOrders() {
        RestaurantQueue queue = RestaurantQueue.withDefaultDishes();

        RestaurantQueue.Order first = queue.addOrder("Ana", RestaurantQueue.item("Pizza", 1));
        RestaurantQueue.Order second = queue.addOrder("Bruno", RestaurantQueue.item("Pasta", 2));
        RestaurantQueue.Order third = queue.addOrder("Carla", RestaurantQueue.item("Salad", 3));

        List<RestaurantQueue.OrderEstimate> estimates = queue.estimateQueue();

        assertEquals(first.getId(), estimates.get(0).getOrderId());
        assertEquals(1, estimates.get(0).getQueuePosition());
        assertEquals(0, estimates.get(0).getEstimatedStartInMinutes());
        assertEquals(20, estimates.get(0).getEstimatedCompletionInMinutes());

        assertEquals(second.getId(), estimates.get(1).getOrderId());
        assertEquals(2, estimates.get(1).getQueuePosition());
        assertEquals(20, estimates.get(1).getEstimatedStartInMinutes());
        assertEquals(56, estimates.get(1).getEstimatedCompletionInMinutes());

        assertEquals(third.getId(), estimates.get(2).getOrderId());
        assertEquals(3, estimates.get(2).getQueuePosition());
        assertEquals(56, estimates.get(2).getEstimatedStartInMinutes());
        assertEquals(80, estimates.get(2).getEstimatedCompletionInMinutes());
    }

    @Test
    void ignoresDoneAndCancelledOrdersWhenEstimatingTheQueue() {
        RestaurantQueue queue = RestaurantQueue.withDefaultDishes();

        RestaurantQueue.Order done = queue.addOrder("Ana", RestaurantQueue.item("Pizza", 1));
        RestaurantQueue.Order active = queue.addOrder("Bruno", RestaurantQueue.item("Pasta", 1));
        RestaurantQueue.Order cancelled = queue.addOrder("Carla", RestaurantQueue.item("Burger", 1));
        RestaurantQueue.Order waiting = queue.addOrder("Davi", RestaurantQueue.item("Salad", 1));

        done.markDone();
        active.markInProgress();
        cancelled.cancel();

        List<RestaurantQueue.OrderEstimate> estimates = queue.estimateQueue();

        assertEquals(2, estimates.size());
        assertEquals(active.getId(), estimates.get(0).getOrderId());
        assertEquals(0, estimates.get(0).getEstimatedStartInMinutes());
        assertEquals(18, estimates.get(0).getEstimatedCompletionInMinutes());
        assertEquals(waiting.getId(), estimates.get(1).getOrderId());
        assertEquals(18, estimates.get(1).getEstimatedStartInMinutes());
        assertEquals(26, estimates.get(1).getEstimatedCompletionInMinutes());
    }

    @Test
    void allowsCustomDishes() {
        RestaurantQueue queue = new RestaurantQueue();
        queue.addDish("Risotto", 22);

        RestaurantQueue.OrderEstimate estimate = queue.estimateOrder(
                queue.addOrder("Ana", RestaurantQueue.item("Risotto", 3)).getId());

        assertEquals(66, estimate.getTotalPreparationTimeMinutes());
        assertDish(estimate.getDishes().get(0), "Risotto", 3, 22, 66);
    }

    @Test
    void rejectsUnknownDish() {
        RestaurantQueue queue = RestaurantQueue.withDefaultDishes();

        assertThrows(IllegalArgumentException.class, () ->
                queue.addOrder("Ana", RestaurantQueue.item("Sushi", 1)));
    }

    @Test
    void rejectsInvalidDishPreparationTime() {
        RestaurantQueue queue = new RestaurantQueue();

        assertThrows(IllegalArgumentException.class, () -> queue.addDish("Soup", 0));
    }

    private static void assertDish(RestaurantQueue.DishEstimate dish,
                                   String dishName,
                                   int quantity,
                                   int unitPreparationTime,
                                   int totalPreparationTime) {
        assertEquals(dishName, dish.getDishName());
        assertEquals(quantity, dish.getQuantity());
        assertEquals(unitPreparationTime, dish.getUnitPreparationTimeMinutes());
        assertEquals(totalPreparationTime, dish.getTotalPreparationTimeMinutes());
    }
}
