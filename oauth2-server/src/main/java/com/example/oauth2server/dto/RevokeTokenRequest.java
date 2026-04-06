package com.example.oauth2server.dto;

import javax.validation.constraints.NotBlank;

public class RevokeTokenRequest {

    @NotBlank
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
