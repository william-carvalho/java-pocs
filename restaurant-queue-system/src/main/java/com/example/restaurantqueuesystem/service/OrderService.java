package com.example.restaurantqueuesystem.service;

import com.example.restaurantqueuesystem.dto.CreateOrderItemRequest;
import com.example.restaurantqueuesystem.dto.CreateOrderRequest;
import com.example.restaurantqueuesystem.dto.OrderEstimateResponse;
import com.example.restaurantqueuesystem.dto.OrderResponse;
import com.example.restaurantqueuesystem.dto.UpdateOrderStatusRequest;
import com.example.restaurantqueuesystem.entity.Dish;
import com.example.restaurantqueuesystem.entity.OrderItem;
import com.example.restaurantqueuesystem.entity.RestaurantOrder;
import com.example.restaurantqueuesystem.enums.OrderStatus;
import com.example.restaurantqueuesystem.exception.ResourceNotFoundException;
import com.example.restaurantqueuesystem.repository.RestaurantOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final RestaurantOrderRepository orderRepository;
    private final DishService dishService;
    private final QueueEstimationService queueEstimationService;

    public OrderService(RestaurantOrderRepository orderRepository,
                        DishService dishService,
                        QueueEstimationService queueEstimationService) {
        this.orderRepository = orderRepository;
        this.dishService = dishService;
        this.queueEstimationService = queueEstimationService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        RestaurantOrder order = new RestaurantOrder();
        order.setCustomerName(normalizeCustomerName(request.getCustomerName()));
        order.setStatus(OrderStatus.WAITING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            Dish dish = dishService.findDishOrThrow(itemRequest.getDishId());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setDish(dish);
            item.setQuantity(itemRequest.getQuantity());
            item.setItemPreparationTimeMinutes(dish.getPreparationTimeMinutes() * itemRequest.getQuantity());
            items.add(item);
        }

        order.setItems(items);
        RestaurantOrder savedOrder = orderRepository.save(order);
        return queueEstimationService.buildOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        RestaurantOrder order = findOrderOrThrow(orderId);
        return queueEstimationService.buildOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        RestaurantOrder order = findOrderOrThrow(orderId);
        order.setStatus(request.getStatus());
        return queueEstimationService.buildOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderEstimateResponse getEstimate(Long orderId) {
        RestaurantOrder order = findOrderOrThrow(orderId);
        return queueEstimationService.buildEstimateResponse(order);
    }

    @Transactional(readOnly = true)
    public RestaurantOrder findOrderOrThrow(Long orderId) {
        return orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for id " + orderId));
    }

    private String normalizeCustomerName(String customerName) {
        if (customerName == null) {
            return null;
        }
        String trimmed = customerName.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
