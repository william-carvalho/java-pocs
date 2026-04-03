package com.example.hibernateslowquerydetector.dto;

import java.time.LocalDateTime;

import com.example.hibernateslowquerydetector.entity.SlowQueryRecord;

public class SlowQueryResponse {

    private Long id;
    private String sqlText;
    private long executionTimeMs;
    private long thresholdMs;
    private LocalDateTime detectedAt;
    private String queryType;
    private String source;

    public static SlowQueryResponse from(SlowQueryRecord record) {
        SlowQueryResponse response = new SlowQueryResponse();
        response.id = record.getId();
        response.sqlText = record.getSqlText();
        response.executionTimeMs = record.getExecutionTimeMs();
        response.thresholdMs = record.getThresholdMs();
        response.detectedAt = record.getDetectedAt();
        response.queryType = record.getQueryType();
        response.source = record.getSource();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getSqlText() {
        return sqlText;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public long getThresholdMs() {
        return thresholdMs;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getSource() {
        return source;
    }
}

