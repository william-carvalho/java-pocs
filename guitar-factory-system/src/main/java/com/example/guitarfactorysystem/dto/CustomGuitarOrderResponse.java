package com.example.guitarfactorysystem.dto;

import com.example.guitarfactorysystem.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CustomGuitarOrderResponse {

    private Long orderId;
    private String customerName;
    private GuitarModelSummaryResponse guitarModel;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<CustomGuitarOrderItemResponse> items;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private Long workOrderId;
    private String workOrderNumber;

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

    public GuitarModelSummaryResponse getGuitarModel() {
        return guitarModel;
    }

    public void setGuitarModel(GuitarModelSummaryResponse guitarModel) {
        this.guitarModel = guitarModel;
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

    public List<CustomGuitarOrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CustomGuitarOrderItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }
}
