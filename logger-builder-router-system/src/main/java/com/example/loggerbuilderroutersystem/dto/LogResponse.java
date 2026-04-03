package com.example.loggerbuilderroutersystem.dto;

import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.enums.LogMode;
import java.time.Instant;

public class LogResponse {

    private String status;
    private String message;
    private LogDestination destination;
    private LogMode mode;
    private Instant processedAt;
    private Instant acceptedAt;

    public static LogResponse processed(LogDestination destination, LogMode mode, Instant processedAt) {
        LogResponse response = new LogResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Log processed successfully");
        response.setDestination(destination);
        response.setMode(mode);
        response.setProcessedAt(processedAt);
        return response;
    }

    public static LogResponse accepted(LogDestination destination, LogMode mode, Instant acceptedAt) {
        LogResponse response = new LogResponse();
        response.setStatus("ACCEPTED");
        response.setMessage("Log accepted for asynchronous processing");
        response.setDestination(destination);
        response.setMode(mode);
        response.setAcceptedAt(acceptedAt);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogDestination getDestination() {
        return destination;
    }

    public void setDestination(LogDestination destination) {
        this.destination = destination;
    }

    public LogMode getMode() {
        return mode;
    }

    public void setMode(LogMode mode) {
        this.mode = mode;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
}
