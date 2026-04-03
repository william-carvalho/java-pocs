package com.example.hibernateslowquerydetector.config;

import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.slow-query")
public class SlowQueryProperties {

    @Min(1)
    private long thresholdMs = 200L;

    public long getThresholdMs() {
        return thresholdMs;
    }

    public void setThresholdMs(long thresholdMs) {
        this.thresholdMs = thresholdMs;
    }
}

