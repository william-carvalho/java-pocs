package com.example.restaurantqueuesystem.dto;

import com.example.restaurantqueuesystem.enums.OrderStatus;

import java.time.LocalDateTime;

public class QueueOrderResponse {

    private Long orderId;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Integer queuePosition;
    private Integer totalPreparationTime;
    private Integer estimatedStartInMinutes;
    private Integer estimatedCompletionInMinutes;
    private Integer remainingTimeInMinutes;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public Integer getTotalPreparationTime() {
        return totalPreparationTime;
    }

    public void setTotalPreparationTime(Integer totalPreparationTime) {
        this.totalPreparationTime = totalPreparationTime;
    }

    public Integer getEstimatedStartInMinutes() {
        return estimatedStartInMinutes;
    }

    public void setEstimatedStartInMinutes(Integer estimatedStartInMinutes) {
        this.estimatedStartInMinutes = estimatedStartInMinutes;
    }

    public Integer getEstimatedCompletionInMinutes() {
        return estimatedCompletionInMinutes;
    }

    public void setEstimatedCompletionInMinutes(Integer estimatedCompletionInMinutes) {
        this.estimatedCompletionInMinutes = estimatedCompletionInMinutes;
    }

    public Integer getRemainingTimeInMinutes() {
        return remainingTimeInMinutes;
    }

    public void setRemainingTimeInMinutes(Integer remainingTimeInMinutes) {
        this.remainingTimeInMinutes = remainingTimeInMinutes;
    }
}
