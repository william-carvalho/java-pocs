package com.example.loggerbuilderroutersystem.dto;

import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.enums.LogLevel;
import com.example.loggerbuilderroutersystem.enums.LogMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LogRequest {

    @NotBlank(message = "message is required")
    private String message;

    @NotNull(message = "level is required")
    private LogLevel level;

    @NotNull(message = "destination is required")
    private LogDestination destination;

    @NotNull(message = "mode is required")
    private LogMode mode;

    private Instant timestamp;

    private Map<String, String> metadata;

    public static LogRequest copyOf(LogRequest request) {
        LogRequest copy = new LogRequest();
        copy.setMessage(request.getMessage());
        copy.setLevel(request.getLevel());
        copy.setDestination(request.getDestination());
        copy.setMode(request.getMode());
        copy.setTimestamp(request.getTimestamp());
        copy.setMetadata(request.getMetadata() == null ? null : new HashMap<String, String>(request.getMetadata()));
        return copy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
