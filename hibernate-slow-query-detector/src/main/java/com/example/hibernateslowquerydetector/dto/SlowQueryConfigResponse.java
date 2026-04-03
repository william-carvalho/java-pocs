package com.example.hibernateslowquerydetector.dto;

public class SlowQueryConfigResponse {

    private final long thresholdMs;

    public SlowQueryConfigResponse(long thresholdMs) {
        this.thresholdMs = thresholdMs;
    }

    public long getThresholdMs() {
        return thresholdMs;
    }
}

