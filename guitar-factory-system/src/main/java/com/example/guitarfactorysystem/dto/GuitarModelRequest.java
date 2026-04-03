package com.example.guitarfactorysystem.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class GuitarModelRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "basePrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "basePrice must be greater than zero")
    private BigDecimal basePrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}
