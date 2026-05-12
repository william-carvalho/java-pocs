package com.example.loggerrouter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class LoggerBuilderRouter {

    private final Map<String, LogSink> routes;
    private final Executor asyncExecutor;
    private final Clock clock;

    private LoggerBuilderRouter(Map<String, LogSink> routes, Executor asyncExecutor, Clock clock) {
        this.routes = Collections.unmodifiableMap(new LinkedHashMap<String, LogSink>(routes));
        this.asyncExecutor = asyncExecutor;
        this.clock = clock;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static EventBuilder event() {
        return new EventBuilder();
    }

    public LogResult log(LogEvent event) {
        Objects.requireNonNull(event, "event");
        final LogSink sink = routes.get(event.destination());
        if (sink == null) {
            throw new IllegalArgumentException("No route configured for destination: " + event.destination());
        }

        if (event.mode() == Mode.ASYNC) {
            CompletableFuture<Void> completion = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    sink.write(event);
                }
            }, asyncExecutor);
            return LogResult.accepted(event.destination(), event.mode(), Instant.now(clock), completion);
        }

        sink.write(event);
        return LogResult.success(event.destination(), event.mode(), Instant.now(clock));
    }

    public Map<String, LogSink> routes() {
        return routes;
    }

    public static final class Builder {
        private final Map<String, LogSink> routes = new LinkedHashMap<String, LogSink>();
        private Executor asyncExecutor = ForkJoinPool.commonPool();
        private Clock clock = Clock.systemUTC();

        public Builder routeToFileSystem(String destination, Path logFile) {
            return route(destination, new FileSystemSink(logFile, clock));
        }

        public Builder routeToFileSystem(String destination, String logFile) {
            return routeToFileSystem(destination, Paths.get(logFile));
        }

        public Builder routeToInMemory(String destination) {
            return route(destination, new InMemorySink());
        }

        public Builder route(String destination, LogSink sink) {
            routes.put(normalizeDestination(destination), Objects.requireNonNull(sink, "sink"));
            return this;
        }

        public Builder asyncExecutor(Executor asyncExecutor) {
            this.asyncExecutor = Objects.requireNonNull(asyncExecutor, "asyncExecutor");
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
            return this;
        }

        public LoggerBuilderRouter build() {
            if (routes.isEmpty()) {
                throw new IllegalStateException("At least one log route is required");
            }
            return new LoggerBuilderRouter(routes, asyncExecutor, clock);
        }
    }

    public static final class EventBuilder {
        private Level level = Level.INFO;
        private String message;
        private String destination = "FS";
        private Mode mode = Mode.SYNC;
        private final Map<String, String> metadata = new LinkedHashMap<String, String>();

        public EventBuilder level(Level level) {
            this.level = Objects.requireNonNull(level, "level");
            return this;
        }

        public EventBuilder message(String message) {
            this.message = requireText(message, "message");
            return this;
        }

        public EventBuilder destination(String destination) {
            this.destination = normalizeDestination(destination);
            return this;
        }

        public EventBuilder sync() {
            this.mode = Mode.SYNC;
            return this;
        }

        public EventBuilder async() {
            this.mode = Mode.ASYNC;
            return this;
        }

        public EventBuilder metadata(String key, String value) {
            metadata.put(requireText(key, "metadata key"), Objects.requireNonNull(value, "metadata value"));
            return this;
        }

        public LogEvent build() {
            return new LogEvent(level, requireText(message, "message"), destination, mode, metadata);
        }
    }

    public static final class LogEvent {
        private final Level level;
        private final String message;
        private final String destination;
        private final Mode mode;
        private final Map<String, String> metadata;

        private LogEvent(Level level, String message, String destination, Mode mode, Map<String, String> metadata) {
            this.level = Objects.requireNonNull(level, "level");
            this.message = requireText(message, "message");
            this.destination = normalizeDestination(destination);
            this.mode = Objects.requireNonNull(mode, "mode");
            this.metadata = Collections.unmodifiableMap(new LinkedHashMap<String, String>(metadata));
        }

        public Level level() {
            return level;
        }

        public String message() {
            return message;
        }

        public String destination() {
            return destination;
        }

        public Mode mode() {
            return mode;
        }

        public Map<String, String> metadata() {
            return metadata;
        }

        public String format(Clock clock) {
            return Instant.now(clock) + " level=" + level
                    + " destination=" + destination
                    + " message=\"" + message + "\""
                    + " metadata=" + metadata;
        }
    }

    public static final class LogResult {
        private final Status status;
        private final String destination;
        private final Mode mode;
        private final Instant timestamp;
        private final CompletableFuture<Void> completion;

        private LogResult(Status status, String destination, Mode mode, Instant timestamp, CompletableFuture<Void> completion) {
            this.status = status;
            this.destination = destination;
            this.mode = mode;
            this.timestamp = timestamp;
            this.completion = completion;
        }

        private static LogResult success(String destination, Mode mode, Instant timestamp) {
            return new LogResult(Status.SUCCESS, destination, mode, timestamp, CompletableFuture.completedFuture(null));
        }

        private static LogResult accepted(String destination, Mode mode, Instant timestamp, CompletableFuture<Void> completion) {
            return new LogResult(Status.ACCEPTED, destination, mode, timestamp, completion);
        }

        public Status status() {
            return status;
        }

        public String destination() {
            return destination;
        }

        public Mode mode() {
            return mode;
        }

        public Instant timestamp() {
            return timestamp;
        }

        public CompletableFuture<Void> completion() {
            return completion;
        }
    }

    public interface LogSink {
        void write(LogEvent event);
    }

    public static final class FileSystemSink implements LogSink {
        private final Path logFile;
        private final Clock clock;

        public FileSystemSink(Path logFile) {
            this(logFile, Clock.systemUTC());
        }

        public FileSystemSink(Path logFile, Clock clock) {
            this.logFile = Objects.requireNonNull(logFile, "logFile");
            this.clock = Objects.requireNonNull(clock, "clock");
        }

        @Override
        public void write(LogEvent event) {
            try {
                Path parent = logFile.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.write(
                        logFile,
                        Collections.singletonList(event.format(clock)),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (IOException exception) {
                throw new UncheckedIOException("Failed to write log to file system", exception);
            }
        }
    }

    public static final class InMemorySink implements LogSink {
        private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<LogEvent>());

        @Override
        public void write(LogEvent event) {
            events.add(event);
        }

        public List<LogEvent> events() {
            synchronized (events) {
                return Collections.unmodifiableList(new ArrayList<LogEvent>(events));
            }
        }
    }

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    public enum Mode {
        SYNC, ASYNC
    }

    public enum Status {
        SUCCESS, ACCEPTED
    }

    private static String normalizeDestination(String destination) {
        return requireText(destination, "destination").trim().toUpperCase();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    public static void main(String[] args) {
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .routeToFileSystem("FS", Paths.get("logs/app.log"))
                .routeToInMemory("ELK")
                .route("CUSTOM", new LogSink() {
                    @Override
                    public void write(LogEvent event) {
                        System.out.println("CUSTOM " + event.format(Clock.systemUTC()));
                    }
                })
                .build();

        logger.log(LoggerBuilderRouter.event()
                .destination("FS")
                .level(Level.INFO)
                .message("Logger Builder Router started")
                .metadata("module", "demo")
                .sync()
                .build());
    }
}
