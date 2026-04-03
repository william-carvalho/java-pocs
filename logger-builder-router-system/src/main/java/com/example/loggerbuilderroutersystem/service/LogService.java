package com.example.loggerbuilderroutersystem.service;

import com.example.loggerbuilderroutersystem.dto.LogRequest;
import com.example.loggerbuilderroutersystem.dto.LogResponse;
import com.example.loggerbuilderroutersystem.enums.LogMode;
import com.example.loggerbuilderroutersystem.router.LogRouter;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

    private final LogRouter logRouter;
    private final TaskExecutor taskExecutor;

    public LogService(LogRouter logRouter, @Qualifier("logTaskExecutor") TaskExecutor taskExecutor) {
        this.logRouter = logRouter;
        this.taskExecutor = taskExecutor;
    }

    public LogResponse publish(LogRequest request) {
        LogRequest safeRequest = LogRequest.copyOf(request);
        Instant effectiveTimestamp = safeRequest.getTimestamp() != null ? safeRequest.getTimestamp() : Instant.now();

        if (LogMode.ASYNC.equals(safeRequest.getMode())) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logRouter.route(safeRequest, effectiveTimestamp);
                    } catch (Exception ex) {
                        LOGGER.error("Async log publication failed for destination {}", safeRequest.getDestination(), ex);
                    }
                }
            });
            return LogResponse.accepted(safeRequest.getDestination(), safeRequest.getMode(), Instant.now());
        }

        logRouter.route(safeRequest, effectiveTimestamp);
        return LogResponse.processed(safeRequest.getDestination(), safeRequest.getMode(), Instant.now());
    }
}
