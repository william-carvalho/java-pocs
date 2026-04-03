package com.example.converterframework.model.entity;

import java.math.BigDecimal;

public class ProductEntity {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final String category;

    public ProductEntity(Long id, String name, BigDecimal price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}

