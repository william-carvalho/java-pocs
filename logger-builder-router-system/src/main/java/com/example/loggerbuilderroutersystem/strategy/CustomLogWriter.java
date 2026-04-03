package com.example.loggerbuilderroutersystem.strategy;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomLogWriter implements LogWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogWriter.class);

    @Override
    public LogDestination getDestination() {
        return LogDestination.CUSTOM;
    }

    @Override
    public void write(LogRequest request, Instant effectiveTimestamp) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("channel", "custom-system");
        payload.put("timestamp", effectiveTimestamp);
        payload.put("level", request.getLevel());
        payload.put("message", request.getMessage());
        payload.put("metadata", request.getMetadata());
        LOGGER.info("Custom destination received log: {}", payload);
    }
}
