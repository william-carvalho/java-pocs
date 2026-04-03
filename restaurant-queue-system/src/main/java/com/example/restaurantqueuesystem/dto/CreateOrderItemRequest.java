package com.example.restaurantqueuesystem.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateOrderItemRequest {

    @NotNull(message = "dishId is required")
    private Long dishId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be greater than zero")
    private Integer quantity;

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
