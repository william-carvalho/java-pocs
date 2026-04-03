package com.example.loggerbuilderroutersystem.strategy;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.enums.LogDestination;
import com.example.loggerbuilderroutersystem.exception.LogWriteException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystemLogWriter implements LogWriter {

    private final Lock writeLock = new ReentrantLock();
    private final Path logFilePath;

    public FileSystemLogWriter(@Value("${app.logging.fs.path:logs/app.log}") String logFilePath) {
        this.logFilePath = Paths.get(logFilePath);
    }

    @Override
    public LogDestination getDestination() {
        return LogDestination.FS;
    }

    @Override
    public void write(LogRequest request, Instant effectiveTimestamp) {
        writeLock.lock();
        try {
            Path parent = logFilePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(
                    logFilePath,
                    buildLine(request, effectiveTimestamp).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException ex) {
            throw new LogWriteException("Failed to write log to file system", ex);
        } finally {
            writeLock.unlock();
        }
    }

    private String buildLine(LogRequest request, Instant effectiveTimestamp) {
        String metadata = formatMetadata(request.getMetadata());
        return new StringBuilder()
                .append('[').append(effectiveTimestamp).append(']')
                .append(' ')
                .append('[').append(request.getLevel()).append(']')
                .append(' ')
                .append(request.getMessage())
                .append(" | metadata=")
                .append(metadata)
                .append(System.lineSeparator())
                .toString();
    }

    private String formatMetadata(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }
        return metadata.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
