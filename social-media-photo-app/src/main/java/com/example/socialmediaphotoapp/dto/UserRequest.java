package com.example.socialmediaphotoapp.dto;

import javax.validation.constraints.NotBlank;

public class UserRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "username is required")
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

