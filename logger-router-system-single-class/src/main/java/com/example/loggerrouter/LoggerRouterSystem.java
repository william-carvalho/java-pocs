package com.example.loggerrouter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class LoggerRouterSystem {
    private final Map<String, LogSink> sinks;
    private final ExecutorService executor;
    private final List<Future<?>> asyncTasks = Collections.synchronizedList(new ArrayList<Future<?>>());

    private LoggerRouterSystem(Map<String, LogSink> sinks, ExecutorService executor) {
        this.sinks = Collections.unmodifiableMap(new LinkedHashMap<String, LogSink>(sinks));
        this.executor = executor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LogEventBuilder event() {
        return new LogEventBuilder();
    }

    public LogResult log(LogEvent event) {
        validateEvent(event);
        final LogSink sink = sinks.get(event.getDestination());
        if (sink == null) {
            throw new IllegalArgumentException("Unknown log destination: " + event.getDestination());
        }

        if (event.getMode() == LogMode.ASYNC) {
            Future<?> future = executor.submit(new Runnable() {
                public void run() {
                    sink.write(event);
                }
            });
            asyncTasks.add(future);
            return LogResult.accepted(event.getDestination(), event.getMode());
        }

        sink.write(event);
        return LogResult.success(event.getDestination(), event.getMode());
    }

    public void awaitAsync() {
        List<Future<?>> snapshot;
        synchronized (asyncTasks) {
            snapshot = new ArrayList<Future<?>>(asyncTasks);
            asyncTasks.clear();
        }
        for (Future<?> task : snapshot) {
            try {
                task.get();
            } catch (Exception ex) {
                throw new IllegalStateException("Async log failed", ex);
            }
        }
    }

    public List<String> destinations() {
        return Collections.unmodifiableList(new ArrayList<String>(sinks.keySet()));
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private static void validateEvent(LogEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event is required");
        }
        if (isBlank(event.getDestination())) {
            throw new IllegalArgumentException("destination is required");
        }
        if (event.getLevel() == null) {
            throw new IllegalArgumentException("level is required");
        }
        if (event.getMode() == null) {
            throw new IllegalArgumentException("mode is required");
        }
        if (isBlank(event.getMessage())) {
            throw new IllegalArgumentException("message is required");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public enum LogMode {
        SYNC,
        ASYNC
    }

    public interface LogSink {
        void write(LogEvent event);
    }

    public static final class Builder {
        private final Map<String, LogSink> sinks = new LinkedHashMap<String, LogSink>();
        private ExecutorService executor = Executors.newSingleThreadExecutor();

        public Builder route(String destination, LogSink sink) {
            if (isBlank(destination)) {
                throw new IllegalArgumentException("destination is required");
            }
            if (sink == null) {
                throw new IllegalArgumentException("sink is required");
            }
            sinks.put(destination.trim().toUpperCase(), sink);
            return this;
        }

        public Builder routeToFileSystem(String destination, Path logFile) {
            return route(destination, new FileSystemLogSink(logFile));
        }

        public Builder routeToElk(String destination) {
            return route(destination, new InMemoryLogSink());
        }

        public Builder routeToMemory(String destination, InMemoryLogSink sink) {
            return route(destination, sink);
        }

        public Builder executor(ExecutorService executor) {
            if (executor == null) {
                throw new IllegalArgumentException("executor is required");
            }
            this.executor = executor;
            return this;
        }

        public LoggerRouterSystem build() {
            if (sinks.isEmpty()) {
                throw new IllegalStateException("at least one route is required");
            }
            return new LoggerRouterSystem(sinks, executor);
        }
    }

    public static final class LogEvent {
        private final String destination;
        private final LogLevel level;
        private final LogMode mode;
        private final String message;
        private final Map<String, String> metadata;
        private final LocalDateTime createdAt;

        private LogEvent(String destination, LogLevel level, LogMode mode, String message, Map<String, String> metadata) {
            this.destination = destination == null ? null : destination.trim().toUpperCase();
            this.level = level;
            this.mode = mode;
            this.message = message;
            this.metadata = Collections.unmodifiableMap(new LinkedHashMap<String, String>(metadata));
            this.createdAt = LocalDateTime.now();
        }

        public String formatLine() {
            return createdAt + " " + level + " " + message + " " + metadata;
        }

        public String toStructuredJson() {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"timestamp\":\"").append(createdAt).append("\",");
            json.append("\"level\":\"").append(level).append("\",");
            json.append("\"message\":\"").append(escape(message)).append("\",");
            json.append("\"destination\":\"").append(destination).append("\",");
            json.append("\"metadata\":{");
            boolean first = true;
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(escape(entry.getKey())).append("\":\"")
                        .append(escape(entry.getValue())).append("\"");
                first = false;
            }
            json.append("}}");
            return json.toString();
        }

        private static String escape(String value) {
            return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
        }

        public String getDestination() {
            return destination;
        }

        public LogLevel getLevel() {
            return level;
        }

        public LogMode getMode() {
            return mode;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    public static final class LogEventBuilder {
        private String destination;
        private LogLevel level = LogLevel.INFO;
        private LogMode mode = LogMode.SYNC;
        private String message;
        private final Map<String, String> metadata = new LinkedHashMap<String, String>();

        public LogEventBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public LogEventBuilder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public LogEventBuilder sync() {
            this.mode = LogMode.SYNC;
            return this;
        }

        public LogEventBuilder async() {
            this.mode = LogMode.ASYNC;
            return this;
        }

        public LogEventBuilder message(String message) {
            this.message = message;
            return this;
        }

        public LogEventBuilder metadata(String key, String value) {
            metadata.put(key, value);
            return this;
        }

        public LogEvent build() {
            return new LogEvent(destination, level, mode, message, metadata);
        }
    }

    public static final class LogResult {
        private final String status;
        private final String message;
        private final String destination;
        private final LogMode mode;
        private final LocalDateTime processedAt;
        private final LocalDateTime acceptedAt;

        private LogResult(String status, String message, String destination, LogMode mode, LocalDateTime processedAt, LocalDateTime acceptedAt) {
            this.status = status;
            this.message = message;
            this.destination = destination;
            this.mode = mode;
            this.processedAt = processedAt;
            this.acceptedAt = acceptedAt;
        }

        private static LogResult success(String destination, LogMode mode) {
            return new LogResult("SUCCESS", "Log processed successfully", destination, mode, LocalDateTime.now(), null);
        }

        private static LogResult accepted(String destination, LogMode mode) {
            return new LogResult("ACCEPTED", "Log accepted for asynchronous processing", destination, mode, null, LocalDateTime.now());
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getDestination() {
            return destination;
        }

        public LogMode getMode() {
            return mode;
        }

        public LocalDateTime getProcessedAt() {
            return processedAt;
        }

        public LocalDateTime getAcceptedAt() {
            return acceptedAt;
        }
    }

    public static final class FileSystemLogSink implements LogSink {
        private final Path logFile;

        public FileSystemLogSink(Path logFile) {
            if (logFile == null) {
                throw new IllegalArgumentException("logFile is required");
            }
            this.logFile = logFile;
        }

        public synchronized void write(LogEvent event) {
            try {
                Path parent = logFile.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.write(
                        logFile,
                        Collections.singletonList(event.formatLine()),
                        StandardCharsets.UTF_8,
                        Files.exists(logFile)
                                ? new java.nio.file.OpenOption[]{java.nio.file.StandardOpenOption.APPEND}
                                : new java.nio.file.OpenOption[]{java.nio.file.StandardOpenOption.CREATE});
            } catch (IOException ex) {
                throw new IllegalStateException("Could not write log file", ex);
            }
        }

        public Path getLogFile() {
            return logFile;
        }
    }

    public static final class InMemoryLogSink implements LogSink {
        private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<LogEvent>());
        private final List<String> structuredPayloads = Collections.synchronizedList(new ArrayList<String>());

        public void write(LogEvent event) {
            events.add(event);
            structuredPayloads.add(event.toStructuredJson());
        }

        public List<LogEvent> events() {
            synchronized (events) {
                return Collections.unmodifiableList(new ArrayList<LogEvent>(events));
            }
        }

        public List<String> structuredPayloads() {
            synchronized (structuredPayloads) {
                return Collections.unmodifiableList(new ArrayList<String>(structuredPayloads));
            }
        }
    }
}
