package com.example.restaurantqueuesystem.dto;

public class OrderItemResponse {

    private Long dishId;
    private String dishName;
    private Integer quantity;
    private Integer unitPreparationTime;
    private Integer totalItemPreparationTime;

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUnitPreparationTime() {
        return unitPreparationTime;
    }

    public void setUnitPreparationTime(Integer unitPreparationTime) {
        this.unitPreparationTime = unitPreparationTime;
    }

    public Integer getTotalItemPreparationTime() {
        return totalItemPreparationTime;
    }

    public void setTotalItemPreparationTime(Integer totalItemPreparationTime) {
        this.totalItemPreparationTime = totalItemPreparationTime;
    }
}
