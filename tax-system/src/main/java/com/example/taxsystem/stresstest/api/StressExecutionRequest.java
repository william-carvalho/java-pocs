package com.example.taxsystem.stresstest.api;

import com.example.taxsystem.stresstest.domain.StressExecutionMode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class StressExecutionRequest {

    @Min(1)
    private int totalRequests;

    @Min(1)
    @Max(256)
    private int concurrencyLevel;

    @Min(100)
    private int timeoutMillis;

    @Min(0)
    private int rampUpMillis;

    @NotNull
    private StressExecutionMode mode;

    private String scheduledAt;

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public void setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public int getRampUpMillis() {
        return rampUpMillis;
    }

    public void setRampUpMillis(int rampUpMillis) {
        this.rampUpMillis = rampUpMillis;
    }

    public StressExecutionMode getMode() {
        return mode;
    }

    public void setMode(StressExecutionMode mode) {
        this.mode = mode;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
