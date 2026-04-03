package com.example.guitarfactorysystem.dto;

import com.example.guitarfactorysystem.enums.ComponentCategory;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ComponentRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "category is required")
    private ComponentCategory category;

    @NotBlank(message = "specificationValue is required")
    private String specificationValue;

    @NotNull(message = "quantityInStock is required")
    @Min(value = 0, message = "quantityInStock cannot be negative")
    private Integer quantityInStock;

    @NotNull(message = "unitPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice must be greater than zero")
    private BigDecimal unitPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComponentCategory getCategory() {
        return category;
    }

    public void setCategory(ComponentCategory category) {
        this.category = category;
    }

    public String getSpecificationValue() {
        return specificationValue;
    }

    public void setSpecificationValue(String specificationValue) {
        this.specificationValue = specificationValue;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
