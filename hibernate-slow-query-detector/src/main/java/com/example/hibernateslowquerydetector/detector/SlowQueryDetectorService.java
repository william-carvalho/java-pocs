package com.example.hibernateslowquerydetector.detector;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Service;

import com.example.hibernateslowquerydetector.config.SlowQueryProperties;
import com.example.hibernateslowquerydetector.entity.SlowQueryRecord;
import com.example.hibernateslowquerydetector.repository.SlowQueryRecordRepository;

@Service
public class SlowQueryDetectorService {

    private final SlowQueryProperties properties;
    private final SlowQueryRecordRepository slowQueryRecordRepository;

    public SlowQueryDetectorService(SlowQueryProperties properties,
                                    SlowQueryRecordRepository slowQueryRecordRepository) {
        this.properties = properties;
        this.slowQueryRecordRepository = slowQueryRecordRepository;
    }

    public <T> T track(String source, Callable<T> callable) {
        QueryCaptureContext.start();
        long start = System.nanoTime();

        try {
            return callable.call();
        } catch (Exception exception) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            }
            throw new IllegalStateException("Failed to execute tracked query: " + source, exception);
        } finally {
            long executionTimeMs = (System.nanoTime() - start) / 1_000_000L;
            registerIfSlow(source, executionTimeMs);
            QueryCaptureContext.clear();
        }
    }

    private void registerIfSlow(String source, long executionTimeMs) {
        if (executionTimeMs < properties.getThresholdMs()) {
            return;
        }

        String sqlText = resolveSqlText();
        if (sqlText == null || sqlText.trim().isEmpty()) {
            return;
        }

        SlowQueryRecord record = new SlowQueryRecord();
        record.setSqlText(sqlText);
        record.setExecutionTimeMs(executionTimeMs);
        record.setThresholdMs(properties.getThresholdMs());
        record.setQueryType(resolveQueryType(sqlText));
        record.setSource(source);
        slowQueryRecordRepository.save(record);
    }

    private String resolveSqlText() {
        List<String> statements = QueryCaptureContext.getStatements();
        if (statements.isEmpty()) {
            return null;
        }
        return statements.get(statements.size() - 1);
    }

    private String resolveQueryType(String sqlText) {
        String normalized = sqlText.trim();
        if (normalized.isEmpty()) {
            return "UNKNOWN";
        }
        int separatorIndex = normalized.indexOf(' ');
        if (separatorIndex < 0) {
            return normalized.toUpperCase();
        }
        return normalized.substring(0, separatorIndex).toUpperCase();
    }
}
