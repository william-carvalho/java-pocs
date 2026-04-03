package com.example.converterframework.model.dto;

import java.math.BigDecimal;

public class OrderResponse {

    private Long id;
    private String customerName;
    private Integer itemCount;
    private BigDecimal totalAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", itemCount=" + itemCount +
                ", totalAmount=" + totalAmount +
                '}';
    }
}

