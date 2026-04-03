package com.example.loggerbuilderroutersystem.strategy;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.exception.LogWriteException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ElkLogWriter implements LogWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElkLogWriter.class);

    private final ObjectMapper objectMapper;

    public ElkLogWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public LogDestination getDestination() {
        return LogDestination.ELK;
    }

    @Override
    public void write(LogRequest request, Instant effectiveTimestamp) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("timestamp", effectiveTimestamp);
        payload.put("level", request.getLevel());
        payload.put("message", request.getMessage());
        payload.put("destination", getDestination());
        payload.put("metadata", request.getMetadata());

        try {
            LOGGER.info("Simulated ELK publish: {}", objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException ex) {
            throw new LogWriteException("Failed to serialize ELK payload", ex);
        }
    }
}
