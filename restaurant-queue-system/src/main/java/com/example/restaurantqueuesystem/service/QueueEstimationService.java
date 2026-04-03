package com.example.restaurantqueuesystem.service;

import com.example.restaurantqueuesystem.dto.OrderEstimateResponse;
import com.example.restaurantqueuesystem.dto.OrderItemResponse;
import com.example.restaurantqueuesystem.dto.OrderResponse;
import com.example.restaurantqueuesystem.dto.QueueOrderResponse;
import com.example.restaurantqueuesystem.entity.OrderItem;
import com.example.restaurantqueuesystem.entity.RestaurantOrder;
import com.example.restaurantqueuesystem.enums.OrderStatus;
import com.example.restaurantqueuesystem.repository.RestaurantOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueueEstimationService {

    private static final EnumSet<OrderStatus> ACTIVE_STATUSES = EnumSet.of(OrderStatus.WAITING, OrderStatus.IN_PROGRESS);

    private final RestaurantOrderRepository orderRepository;

    public QueueEstimationService(RestaurantOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<QueueOrderResponse> getQueue() {
        List<RestaurantOrder> activeOrders = orderRepository.findByStatusInOrderByCreatedAtAscIdAsc(ACTIVE_STATUSES);
        Map<Long, QueueMetrics> metricsByOrderId = buildMetrics(activeOrders);

        return activeOrders.stream()
                .map(order -> toQueueResponse(order, metricsByOrderId.get(order.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse buildOrderResponse(RestaurantOrder order) {
        QueueMetrics metrics = getMetricsForOrder(order);

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setQueuePosition(metrics.getQueuePosition());
        response.setItems(mapItems(order.getItems()));
        response.setTotalPreparationTime(metrics.getTotalPreparationTime());
        response.setEstimatedStartInMinutes(metrics.getEstimatedStartInMinutes());
        response.setEstimatedCompletionInMinutes(metrics.getEstimatedCompletionInMinutes());
        response.setRemainingTimeInMinutes(metrics.getRemainingTimeInMinutes());
        return response;
    }

    @Transactional(readOnly = true)
    public OrderEstimateResponse buildEstimateResponse(RestaurantOrder order) {
        QueueMetrics metrics = getMetricsForOrder(order);

        OrderEstimateResponse response = new OrderEstimateResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setQueuePosition(metrics.getQueuePosition());
        response.setTotalPreparationTime(metrics.getTotalPreparationTime());
        response.setEstimatedStartInMinutes(metrics.getEstimatedStartInMinutes());
        response.setEstimatedCompletionInMinutes(metrics.getEstimatedCompletionInMinutes());
        response.setRemainingTimeInMinutes(metrics.getRemainingTimeInMinutes());
        return response;
    }

    private QueueMetrics getMetricsForOrder(RestaurantOrder order) {
        int totalPreparationTime = calculateTotalPreparationTime(order);
        if (!ACTIVE_STATUSES.contains(order.getStatus())) {
            return QueueMetrics.finished(totalPreparationTime);
        }

        List<RestaurantOrder> activeOrders = orderRepository.findByStatusInOrderByCreatedAtAscIdAsc(ACTIVE_STATUSES);
        Map<Long, QueueMetrics> metricsByOrderId = buildMetrics(activeOrders);
        return metricsByOrderId.getOrDefault(order.getId(), QueueMetrics.finished(totalPreparationTime));
    }

    private Map<Long, QueueMetrics> buildMetrics(List<RestaurantOrder> orders) {
        Map<Long, QueueMetrics> metricsByOrderId = new HashMap<>();
        int accumulatedMinutes = 0;

        for (int index = 0; index < orders.size(); index++) {
            RestaurantOrder order = orders.get(index);
            int totalPreparationTime = calculateTotalPreparationTime(order);
            int estimatedStart = accumulatedMinutes;
            int estimatedCompletion = estimatedStart + totalPreparationTime;

            QueueMetrics metrics = new QueueMetrics(
                    index + 1,
                    totalPreparationTime,
                    estimatedStart,
                    estimatedCompletion,
                    estimatedCompletion
            );

            metricsByOrderId.put(order.getId(), metrics);
            accumulatedMinutes = estimatedCompletion;
        }

        return metricsByOrderId;
    }

    private List<OrderItemResponse> mapItems(List<OrderItem> items) {
        List<OrderItemResponse> responses = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemResponse response = new OrderItemResponse();
            response.setDishId(item.getDish().getId());
            response.setDishName(item.getDish().getName());
            response.setQuantity(item.getQuantity());
            response.setUnitPreparationTime(item.getDish().getPreparationTimeMinutes());
            response.setTotalItemPreparationTime(item.getItemPreparationTimeMinutes());
            responses.add(response);
        }
        return responses;
    }

    private QueueOrderResponse toQueueResponse(RestaurantOrder order, QueueMetrics metrics) {
        QueueOrderResponse response = new QueueOrderResponse();
        response.setOrderId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setQueuePosition(metrics.getQueuePosition());
        response.setTotalPreparationTime(metrics.getTotalPreparationTime());
        response.setEstimatedStartInMinutes(metrics.getEstimatedStartInMinutes());
        response.setEstimatedCompletionInMinutes(metrics.getEstimatedCompletionInMinutes());
        response.setRemainingTimeInMinutes(metrics.getRemainingTimeInMinutes());
        return response;
    }

    private int calculateTotalPreparationTime(RestaurantOrder order) {
        return order.getItems()
                .stream()
                .mapToInt(OrderItem::getItemPreparationTimeMinutes)
                .sum();
    }

    private static class QueueMetrics {

        private final Integer queuePosition;
        private final Integer totalPreparationTime;
        private final Integer estimatedStartInMinutes;
        private final Integer estimatedCompletionInMinutes;
        private final Integer remainingTimeInMinutes;

        private QueueMetrics(Integer queuePosition,
                             Integer totalPreparationTime,
                             Integer estimatedStartInMinutes,
                             Integer estimatedCompletionInMinutes,
                             Integer remainingTimeInMinutes) {
            this.queuePosition = queuePosition;
            this.totalPreparationTime = totalPreparationTime;
            this.estimatedStartInMinutes = estimatedStartInMinutes;
            this.estimatedCompletionInMinutes = estimatedCompletionInMinutes;
            this.remainingTimeInMinutes = remainingTimeInMinutes;
        }

        private static QueueMetrics finished(Integer totalPreparationTime) {
            return new QueueMetrics(null, totalPreparationTime, null, null, 0);
        }

        public Integer getQueuePosition() {
            return queuePosition;
        }

        public Integer getTotalPreparationTime() {
            return totalPreparationTime;
        }

        public Integer getEstimatedStartInMinutes() {
            return estimatedStartInMinutes;
        }

        public Integer getEstimatedCompletionInMinutes() {
            return estimatedCompletionInMinutes;
        }

        public Integer getRemainingTimeInMinutes() {
            return remainingTimeInMinutes;
        }
    }
}
