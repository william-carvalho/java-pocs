package com.example.loggerbuilderroutersystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.loggerbuilderroutersystem.LoggerBuilderRouter.InMemoryTarget;
import com.example.loggerbuilderroutersystem.LoggerBuilderRouter.LogEvent;
import com.example.loggerbuilderroutersystem.LoggerBuilderRouter.LogLevel;
import com.example.loggerbuilderroutersystem.LoggerBuilderRouter.LogMode;
import com.example.loggerbuilderroutersystem.LoggerBuilderRouter.LogResult;
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
    void writesSyncLogsToFileSystem() throws Exception {
        Path logFile = tempDir.resolve("app.log");
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .withFileSystem("FS", logFile)
                .build();

        LogResult result = logger.log(LoggerBuilderRouter.event()
                .destination("fs")
                .level(LogLevel.INFO)
                .message("user created")
                .metadata("userId", "123")
                .sync()
                .build());

        String content = new String(Files.readAllBytes(logFile), StandardCharsets.UTF_8);
        assertEquals("SUCCESS", result.status());
        assertEquals("FS", result.destination());
        assertEquals(LogMode.SYNC, result.mode());
        assertTrue(content.contains("INFO FS user created"));
        assertTrue(content.contains("userId=123"));
    }

    @Test
    void acceptsAsyncLogsUsingTheSameApi() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        InMemoryTarget elk = new InMemoryTarget();
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .withTarget("ELK", event -> {
                    elk.write(event);
                    latch.countDown();
                })
                .build();

        LogEvent event = LoggerBuilderRouter.event()
                .destination("ELK")
                .level(LogLevel.ERROR)
                .message("payment timeout")
                .async()
                .build();

        LogResult result = logger.log(event);
        result.completion().get(2, TimeUnit.SECONDS);

        assertEquals("ACCEPTED", result.status());
        assertEquals(LogMode.ASYNC, result.mode());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(1, elk.events().size());
    }

    @Test
    void routesToCustomDestination() {
        InMemoryTarget custom = new InMemoryTarget();
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .withTarget("audit", custom)
                .build();

        logger.log(LoggerBuilderRouter.event()
                .destination("AUDIT")
                .message("admin changed role")
                .sync()
                .build());

        assertEquals(1, custom.events().size());
        assertTrue(custom.events().values().iterator().next().metadata().isEmpty());
    }

    @Test
    void rejectsUnknownDestination() {
        LoggerBuilderRouter logger = LoggerBuilderRouter.builder()
                .withElk("ELK")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                logger.log(LoggerBuilderRouter.event()
                        .destination("FS")
                        .message("missing route")
                        .build()));

        assertEquals("Unknown log destination: FS", exception.getMessage());
        assertFalse(logger.targets().containsKey("FS"));
    }
}
