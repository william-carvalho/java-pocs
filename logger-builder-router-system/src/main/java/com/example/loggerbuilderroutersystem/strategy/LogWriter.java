package com.example.loggerbuilderroutersystem.strategy;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import java.time.Instant;

public interface LogWriter {

    LogDestination getDestination();

    void write(LogRequest request, Instant effectiveTimestamp);
}
