package com.example.loggerbuilderroutersystem.router;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.exception.DestinationNotFoundException;
import com.example.loggerbuilderroutersystem.strategy.LogWriter;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LogRouter {

    private final Map<LogDestination, LogWriter> writers;

    public LogRouter(List<LogWriter> availableWriters) {
        this.writers = new EnumMap<LogDestination, LogWriter>(LogDestination.class);
        for (LogWriter writer : availableWriters) {
            this.writers.put(writer.getDestination(), writer);
        }
    }

    public void route(LogRequest request, Instant effectiveTimestamp) {
        LogWriter writer = writers.get(request.getDestination());
        if (writer == null) {
            throw new DestinationNotFoundException("Destination not supported: " + request.getDestination());
        }
        writer.write(request, effectiveTimestamp);
    }
}
