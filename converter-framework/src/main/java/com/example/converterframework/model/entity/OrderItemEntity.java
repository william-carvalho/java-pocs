package com.example.converterframework.model.entity;

public class OrderItemEntity {

    private final ProductEntity product;
    private final Integer quantity;

    public OrderItemEntity(ProductEntity product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

