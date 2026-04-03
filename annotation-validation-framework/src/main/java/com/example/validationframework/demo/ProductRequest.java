package com.example.validationframework.demo;

import com.example.validationframework.annotation.Min;
import com.example.validationframework.annotation.NotBlank;
import com.example.validationframework.annotation.Pattern;
import com.example.validationframework.annotation.Size;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 50, message = "Product name must have at most 50 characters")
    private final String name;

    @Size(min = 10, max = 120, message = "Description must be between 10 and 120 characters")
    private final String description;

    @Min(value = 1, message = "Price must be greater than zero")
    private final Double price;

    @Pattern(regex = "[A-Z]{3}-\\d{4}", message = "SKU must match AAA-9999")
    private final String sku;

    public ProductRequest(String name, String description, Double price, String sku) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
    }
}

