package com.example.restaurantqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class RestaurantQueue {
    private final Map<String, Dish> dishesByName = new LinkedHashMap<String, Dish>();
    private final List<Order> orders = new ArrayList<Order>();
    private long nextOrderId = 1;

    public Dish addDish(String name, int preparationTimeMinutes) {
        Dish dish = new Dish(name, preparationTimeMinutes);
        dishesByName.put(dish.getName(), dish);
        return dish;
    }

    public Order addOrder(String customerName, OrderItemRequest... itemRequests) {
        if (isBlank(customerName)) {
            throw new IllegalArgumentException("customerName is required");
        }
        if (itemRequests == null || itemRequests.length == 0) {
            throw new IllegalArgumentException("order must contain at least one item");
        }

        List<OrderItem> items = new ArrayList<OrderItem>();
        for (OrderItemRequest request : itemRequests) {
            Objects.requireNonNull(request, "item request");
            Dish dish = dishesByName.get(request.dishName);
            if (dish == null) {
                throw new IllegalArgumentException("Unknown dish: " + request.dishName);
            }
            items.add(new OrderItem(dish, request.quantity));
        }

        Order order = new Order(nextOrderId++, customerName, items);
        orders.add(order);
        return order;
    }

    public List<OrderEstimate> estimateQueue() {
        List<OrderEstimate> estimates = new ArrayList<OrderEstimate>();
        int elapsedMinutes = 0;
        int position = 1;

        for (Order order : orders) {
            if (!order.getStatus().countsInQueue()) {
                continue;
            }
            int totalPreparationTime = order.totalPreparationTimeMinutes();
            estimates.add(new OrderEstimate(
                    order.getId(),
                    order.getCustomerName(),
                    position++,
                    elapsedMinutes,
                    elapsedMinutes + totalPreparationTime,
                    totalPreparationTime,
                    order.dishEstimates()));
            elapsedMinutes += totalPreparationTime;
        }

        return Collections.unmodifiableList(estimates);
    }

    public OrderEstimate estimateOrder(long orderId) {
        for (OrderEstimate estimate : estimateQueue()) {
            if (estimate.getOrderId() == orderId) {
                return estimate;
            }
        }
        throw new IllegalArgumentException("Order is not waiting in the queue: " + orderId);
    }

    public List<Dish> dishes() {
        return Collections.unmodifiableList(new ArrayList<Dish>(dishesByName.values()));
    }

    public List<Order> orders() {
        return Collections.unmodifiableList(orders);
    }

    public static OrderItemRequest item(String dishName, int quantity) {
        return new OrderItemRequest(dishName, quantity);
    }

    public static RestaurantQueue withDefaultDishes() {
        RestaurantQueue queue = new RestaurantQueue();
        queue.addDish("Burger", 15);
        queue.addDish("Pizza", 20);
        queue.addDish("Salad", 8);
        queue.addDish("Pasta", 18);
        return queue;
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public enum OrderStatus {
        WAITING,
        IN_PROGRESS,
        DONE,
        CANCELLED;

        private boolean countsInQueue() {
            return this == WAITING || this == IN_PROGRESS;
        }
    }

    public static final class Dish {
        private final String name;
        private final int preparationTimeMinutes;

        private Dish(String name, int preparationTimeMinutes) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("dish name is required");
            }
            if (preparationTimeMinutes <= 0) {
                throw new IllegalArgumentException("preparationTimeMinutes must be greater than zero");
            }
            this.name = name.trim();
            this.preparationTimeMinutes = preparationTimeMinutes;
        }

        public String getName() {
            return name;
        }

        public int getPreparationTimeMinutes() {
            return preparationTimeMinutes;
        }
    }

    public static final class OrderItemRequest {
        private final String dishName;
        private final int quantity;

        private OrderItemRequest(String dishName, int quantity) {
            if (isBlank(dishName)) {
                throw new IllegalArgumentException("dishName is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be greater than zero");
            }
            this.dishName = dishName.trim();
            this.quantity = quantity;
        }
    }

    public static final class OrderItem {
        private final Dish dish;
        private final int quantity;

        private OrderItem(Dish dish, int quantity) {
            this.dish = Objects.requireNonNull(dish, "dish");
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be greater than zero");
            }
            this.quantity = quantity;
        }

        public Dish getDish() {
            return dish;
        }

        public int getQuantity() {
            return quantity;
        }

        public int totalPreparationTimeMinutes() {
            return dish.getPreparationTimeMinutes() * quantity;
        }
    }

    public static final class Order {
        private final long id;
        private final String customerName;
        private final List<OrderItem> items;
        private OrderStatus status = OrderStatus.WAITING;

        private Order(long id, String customerName, List<OrderItem> items) {
            this.id = id;
            this.customerName = customerName;
            this.items = Collections.unmodifiableList(new ArrayList<OrderItem>(items));
        }

        public long getId() {
            return id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void markInProgress() {
            status = OrderStatus.IN_PROGRESS;
        }

        public void markDone() {
            status = OrderStatus.DONE;
        }

        public void cancel() {
            status = OrderStatus.CANCELLED;
        }

        public int totalPreparationTimeMinutes() {
            int total = 0;
            for (OrderItem item : items) {
                total += item.totalPreparationTimeMinutes();
            }
            return total;
        }

        private List<DishEstimate> dishEstimates() {
            List<DishEstimate> estimates = new ArrayList<DishEstimate>();
            for (OrderItem item : items) {
                estimates.add(new DishEstimate(
                        item.getDish().getName(),
                        item.getQuantity(),
                        item.getDish().getPreparationTimeMinutes(),
                        item.totalPreparationTimeMinutes()));
            }
            return estimates;
        }
    }

    public static final class DishEstimate {
        private final String dishName;
        private final int quantity;
        private final int unitPreparationTimeMinutes;
        private final int totalPreparationTimeMinutes;

        private DishEstimate(String dishName, int quantity, int unitPreparationTimeMinutes, int totalPreparationTimeMinutes) {
            this.dishName = dishName;
            this.quantity = quantity;
            this.unitPreparationTimeMinutes = unitPreparationTimeMinutes;
            this.totalPreparationTimeMinutes = totalPreparationTimeMinutes;
        }

        public String getDishName() {
            return dishName;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getUnitPreparationTimeMinutes() {
            return unitPreparationTimeMinutes;
        }

        public int getTotalPreparationTimeMinutes() {
            return totalPreparationTimeMinutes;
        }
    }

    public static final class OrderEstimate {
        private final long orderId;
        private final String customerName;
        private final int queuePosition;
        private final int estimatedStartInMinutes;
        private final int estimatedCompletionInMinutes;
        private final int totalPreparationTimeMinutes;
        private final List<DishEstimate> dishes;

        private OrderEstimate(long orderId,
                              String customerName,
                              int queuePosition,
                              int estimatedStartInMinutes,
                              int estimatedCompletionInMinutes,
                              int totalPreparationTimeMinutes,
                              List<DishEstimate> dishes) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.queuePosition = queuePosition;
            this.estimatedStartInMinutes = estimatedStartInMinutes;
            this.estimatedCompletionInMinutes = estimatedCompletionInMinutes;
            this.totalPreparationTimeMinutes = totalPreparationTimeMinutes;
            this.dishes = Collections.unmodifiableList(new ArrayList<DishEstimate>(dishes));
        }

        public long getOrderId() {
            return orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public int getQueuePosition() {
            return queuePosition;
        }

        public int getEstimatedStartInMinutes() {
            return estimatedStartInMinutes;
        }

        public int getEstimatedCompletionInMinutes() {
            return estimatedCompletionInMinutes;
        }

        public int getTotalPreparationTimeMinutes() {
            return totalPreparationTimeMinutes;
        }

        public List<DishEstimate> getDishes() {
            return dishes;
        }
    }
}
