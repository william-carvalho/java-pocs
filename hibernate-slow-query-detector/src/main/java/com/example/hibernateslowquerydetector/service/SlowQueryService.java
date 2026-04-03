package com.example.hibernateslowquerydetector.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.hibernateslowquerydetector.config.SlowQueryProperties;
import com.example.hibernateslowquerydetector.dto.SlowQueryConfigResponse;
import com.example.hibernateslowquerydetector.dto.SlowQueryResponse;
import com.example.hibernateslowquerydetector.dto.SlowQueryStatsResponse;
import com.example.hibernateslowquerydetector.entity.SlowQueryRecord;
import com.example.hibernateslowquerydetector.exception.ResourceNotFoundException;
import com.example.hibernateslowquerydetector.repository.SlowQueryRecordRepository;

@Service
public class SlowQueryService {

    private final SlowQueryRecordRepository slowQueryRecordRepository;
    private final SlowQueryProperties slowQueryProperties;

    public SlowQueryService(SlowQueryRecordRepository slowQueryRecordRepository,
                            SlowQueryProperties slowQueryProperties) {
        this.slowQueryRecordRepository = slowQueryRecordRepository;
        this.slowQueryProperties = slowQueryProperties;
    }

    public List<SlowQueryResponse> listAll() {
        return slowQueryRecordRepository.findAllByOrderByDetectedAtDesc()
                .stream()
                .map(SlowQueryResponse::from)
                .collect(Collectors.toList());
    }

    public SlowQueryResponse getById(Long id) {
        SlowQueryRecord record = slowQueryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slow query not found for id " + id));
        return SlowQueryResponse.from(record);
    }

    public void clearAll() {
        slowQueryRecordRepository.deleteAll();
    }

    public SlowQueryStatsResponse getStats() {
        List<SlowQueryRecord> records = slowQueryRecordRepository.findAll();
        if (records.isEmpty()) {
            return new SlowQueryStatsResponse(0L, 0.0, 0L, 0L);
        }

        long total = records.size();
        long totalExecutionTime = 0L;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (SlowQueryRecord record : records) {
            long executionTime = record.getExecutionTimeMs();
            totalExecutionTime += executionTime;
            min = Math.min(min, executionTime);
            max = Math.max(max, executionTime);
        }

        return new SlowQueryStatsResponse(total, (double) totalExecutionTime / total, min, max);
    }

    public SlowQueryConfigResponse getConfig() {
        return new SlowQueryConfigResponse(slowQueryProperties.getThresholdMs());
    }
}
