package com.example.hibernateslowquerydetector.dto;

public class DemoQueryResponse {

    private final String message;
    private final Object result;

    public DemoQueryResponse(String message, Object result) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }
}

