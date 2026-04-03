package com.example.restaurantqueuesystem.dto;

import com.example.restaurantqueuesystem.enums.OrderStatus;

import javax.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {

    @NotNull(message = "status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
