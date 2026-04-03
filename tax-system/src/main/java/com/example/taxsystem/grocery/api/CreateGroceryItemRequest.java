package com.example.taxsystem.grocery.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateGroceryItemRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
