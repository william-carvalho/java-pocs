package com.example.loggerrouter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.loggerrouter.LoggerRouterSystem.LogLevel.DEBUG;
import static com.example.loggerrouter.LoggerRouterSystem.LogLevel.ERROR;
import static com.example.loggerrouter.LoggerRouterSystem.LogLevel.INFO;
import static com.example.loggerrouter.LoggerRouterSystem.LogMode.ASYNC;
import static com.example.loggerrouter.LoggerRouterSystem.LogMode.SYNC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerRouterSystemTest {
    @TempDir
    Path tempDir;

    @Test
    void logsSynchronouslyToFileSystem() throws Exception {
        Path logFile = tempDir.resolve("logs/app.log");
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .routeToFileSystem("FS", logFile)
                .build();

        LoggerRouterSystem.LogResult result = logger.log(LoggerRouterSystem.event()
                .destination("FS")
                .level(INFO)
                .message("User created")
                .metadata("userId", "123")
                .sync()
                .build());

        List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(SYNC, result.getMode());
        assertNotNull(result.getProcessedAt());
        assertNull(result.getAcceptedAt());
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("INFO User created"));
        assertTrue(lines.get(0).contains("userId=123"));
        logger.shutdown();
    }

    @Test
    void logsAsynchronouslyUsingSameApi() {
        LoggerRouterSystem.InMemoryLogSink elk = new LoggerRouterSystem.InMemoryLogSink();
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .routeToMemory("ELK", elk)
                .executor(Executors.newSingleThreadExecutor())
                .build();

        LoggerRouterSystem.LogResult result = logger.log(LoggerRouterSystem.event()
                .destination("ELK")
                .level(ERROR)
                .message("Payment timeout")
                .metadata("transactionId", "TX999")
                .async()
                .build());

        logger.awaitAsync();

        assertEquals("ACCEPTED", result.getStatus());
        assertEquals(ASYNC, result.getMode());
        assertNull(result.getProcessedAt());
        assertNotNull(result.getAcceptedAt());
        assertEquals(1, elk.events().size());
        assertEquals("Payment timeout", elk.events().get(0).getMessage());
        logger.shutdown();
    }

    @Test
    void routesToCustomDestination() {
        final AtomicInteger calls = new AtomicInteger();
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .route("CUSTOM", new LoggerRouterSystem.LogSink() {
                    public void write(LoggerRouterSystem.LogEvent event) {
                        if ("Custom event".equals(event.getMessage())) {
                            calls.incrementAndGet();
                        }
                    }
                })
                .build();

        logger.log(LoggerRouterSystem.event()
                .destination("CUSTOM")
                .level(DEBUG)
                .message("Custom event")
                .sync()
                .build());

        assertEquals(1, calls.get());
        logger.shutdown();
    }

    @Test
    void elkSinkStoresStructuredPayload() {
        LoggerRouterSystem.InMemoryLogSink elk = new LoggerRouterSystem.InMemoryLogSink();
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .routeToMemory("ELK", elk)
                .build();

        logger.log(LoggerRouterSystem.event()
                .destination("elk")
                .level(ERROR)
                .message("Quote \"failed\"")
                .metadata("module", "payment")
                .sync()
                .build());

        String payload = elk.structuredPayloads().get(0);
        assertTrue(payload.contains("\"destination\":\"ELK\""));
        assertTrue(payload.contains("\"level\":\"ERROR\""));
        assertTrue(payload.contains("\"message\":\"Quote \\\"failed\\\"\""));
        assertTrue(payload.contains("\"module\":\"payment\""));
        logger.shutdown();
    }

    @Test
    void appendsMultipleFileSystemLogs() throws Exception {
        Path logFile = tempDir.resolve("app.log");
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .routeToFileSystem("FS", logFile)
                .build();

        logger.log(LoggerRouterSystem.event().destination("FS").message("first").sync().build());
        logger.log(LoggerRouterSystem.event().destination("FS").message("second").sync().build());

        assertEquals(2, Files.readAllLines(logFile, StandardCharsets.UTF_8).size());
        logger.shutdown();
    }

    @Test
    void exposesConfiguredDestinations() {
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .routeToMemory("ELK", new LoggerRouterSystem.InMemoryLogSink())
                .route("CUSTOM", new LoggerRouterSystem.InMemoryLogSink())
                .build();

        assertEquals(2, logger.destinations().size());
        assertEquals("ELK", logger.destinations().get(0));
        assertEquals("CUSTOM", logger.destinations().get(1));
        logger.shutdown();
    }

    @Test
    void rejectsUnknownDestinationAndInvalidEvent() {
        LoggerRouterSystem logger = LoggerRouterSystem.builder()
                .routeToMemory("ELK", new LoggerRouterSystem.InMemoryLogSink())
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                logger.log(LoggerRouterSystem.event().destination("FS").message("x").build()));
        assertThrows(IllegalArgumentException.class, () ->
                logger.log(LoggerRouterSystem.event().destination("ELK").message(" ").build()));
        logger.shutdown();
    }

    @Test
    void builderRejectsInvalidRoutesAndEmptyBuild() {
        assertThrows(IllegalStateException.class, () -> LoggerRouterSystem.builder().build());
        assertThrows(IllegalArgumentException.class, () ->
                LoggerRouterSystem.builder().route(" ", new LoggerRouterSystem.InMemoryLogSink()));
        assertThrows(IllegalArgumentException.class, () ->
                LoggerRouterSystem.builder().route("CUSTOM", null));
    }
}
