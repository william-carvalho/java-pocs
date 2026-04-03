package com.example.converterframework.model.entity;

import java.math.BigDecimal;
import java.util.List;

public class OrderEntity {

    private final Long id;
    private final UserEntity customer;
    private final List<OrderItemEntity> items;
    private final BigDecimal totalAmount;

    public OrderEntity(Long id, UserEntity customer, List<OrderItemEntity> items, BigDecimal totalAmount) {
        this.id = id;
        this.customer = customer;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getCustomer() {
        return customer;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}

