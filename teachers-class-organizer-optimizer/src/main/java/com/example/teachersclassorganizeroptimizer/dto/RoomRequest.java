package com.example.teachersclassorganizeroptimizer.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RoomRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "capacity is required")
    @Min(value = 1, message = "capacity must be greater than zero")
    private Integer capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}

