package com.example.calendarsystem.dto;

import javax.validation.constraints.NotBlank;

public class PersonRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

