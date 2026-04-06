package com.example.oauth2server.dto;

public class OAuthErrorResponse {

    private final String error;
    private final String error_description;

    public OAuthErrorResponse(String error, String errorDescription) {
        this.error = error;
        this.error_description = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }
}
