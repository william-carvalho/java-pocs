package com.example.hibernateslowquerydetector.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "slow_query_records")
public class SlowQueryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String sqlText;

    @Column(nullable = false)
    private long executionTimeMs;

    @Column(nullable = false)
    private long thresholdMs;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    @Column
    private String queryType;

    @Column
    private String source;

    @PrePersist
    public void prePersist() {
        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public long getThresholdMs() {
        return thresholdMs;
    }

    public void setThresholdMs(long thresholdMs) {
        this.thresholdMs = thresholdMs;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

