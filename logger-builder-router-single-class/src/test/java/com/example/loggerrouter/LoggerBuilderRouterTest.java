package com.example.loggerrouter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.loggerrouter.LoggerBuilderRouter.InMemorySink;
import com.example.loggerrouter.LoggerBuilderRouter.Level;
import com.example.loggerrouter.LoggerBuilderRouter.LogResult;
import com.example.loggerrouter.LoggerBuilderRouter.Mode;
import com.example.loggerrouter.LoggerBuilderRouter.Status;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LoggerBuilderRouterTest {

    @TempDir
    Path tempDir;

    @Test
    void logsToFileSystemSynchronously() throws Exception {
        Path logFile = tempDir.resolve("app.log");
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .routeToFileSystem("FS", logFile)
                .build();

        LogResult result = logger.log(LoggerBuilderRouter.event()
                .destination("fs")
                .level(Level.INFO)
                .message("user created")
                .metadata("userId", "123")
                .sync()
                .build());

        String content = new String(Files.readAllBytes(logFile), StandardCharsets.UTF_8);

        assertEquals(Status.SUCCESS, result.status());
        assertEquals("FS", result.destination());
        assertEquals(Mode.SYNC, result.mode());
        assertTrue(content.contains("level=INFO"));
        assertTrue(content.contains("destination=FS"));
        assertTrue(content.contains("message=\"user created\""));
        assertTrue(content.contains("userId=123"));
    }

    @Test
    void logsToElkStyleDestinationAsynchronouslyUsingSameApi() throws Exception {
        final InMemorySink elk = new InMemorySink();
        final CountDownLatch latch = new CountDownLatch(1);
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .route("ELK", event -> {
                    elk.write(event);
                    latch.countDown();
                })
                .build();

        LogResult result = logger.log(LoggerBuilderRouter.event()
                .destination("ELK")
                .level(Level.ERROR)
                .message("payment timeout")
                .metadata("transactionId", "TX-9")
                .async()
                .build());

        result.completion().get(2, TimeUnit.SECONDS);

        assertEquals(Status.ACCEPTED, result.status());
        assertEquals(Mode.ASYNC, result.mode());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(1, elk.events().size());
        assertEquals("payment timeout", elk.events().get(0).message());
    }

    @Test
    void routesToAnyCustomLogSystem() {
        InMemorySink audit = new InMemorySink();
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .route("AUDIT", audit)
                .build();

        logger.log(LoggerBuilderRouter.event()
                .destination("audit")
                .level(Level.WARN)
                .message("role changed")
                .sync()
                .build());

        assertEquals(1, audit.events().size());
        assertEquals(Level.WARN, audit.events().get(0).level());
        assertEquals("AUDIT", audit.events().get(0).destination());
    }

    @Test
    void rejectsUnknownDestination() {
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .routeToInMemory("ELK")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                logger.log(LoggerBuilderRouter.event()
                        .destination("FS")
                        .message("no route")
                        .build()));

        assertEquals("No route configured for destination: FS", exception.getMessage());
        assertFalse(logger.routes().containsKey("FS"));
    }
}
