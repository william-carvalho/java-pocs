package com.example.restaurantqueuesystem.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DishRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "preparationTimeMinutes is required")
    @Min(value = 1, message = "preparationTimeMinutes must be greater than zero")
    private Integer preparationTimeMinutes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }
}
