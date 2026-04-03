package com.example.guitarfactorysystem.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateCustomGuitarItemRequest {

    @NotNull(message = "componentId is required")
    private Long componentId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be greater than zero")
    private Integer quantity;

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
