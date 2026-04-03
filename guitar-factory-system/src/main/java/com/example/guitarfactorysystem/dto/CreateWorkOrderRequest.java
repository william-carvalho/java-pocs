package com.example.guitarfactorysystem.dto;

import javax.validation.constraints.NotNull;

public class CreateWorkOrderRequest {

    @NotNull(message = "customGuitarOrderId is required")
    private Long customGuitarOrderId;

    public Long getCustomGuitarOrderId() {
        return customGuitarOrderId;
    }

    public void setCustomGuitarOrderId(Long customGuitarOrderId) {
        this.customGuitarOrderId = customGuitarOrderId;
    }
}
