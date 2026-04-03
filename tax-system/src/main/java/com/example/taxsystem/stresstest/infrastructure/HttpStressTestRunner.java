package com.example.taxsystem.stresstest.infrastructure;

import com.example.taxsystem.common.config.StressExecutionContext;
import com.example.taxsystem.stresstest.domain.StressTestConfig;
import com.example.taxsystem.stresstest.domain.StressTestReport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class HttpStressTestRunner {

    private final StressHttpClient stressHttpClient;
    private final StressReportFactory stressReportFactory;
    private final ConsoleStressReportPrinter consoleStressReportPrinter;

    public HttpStressTestRunner(StressHttpClient stressHttpClient,
                                StressReportFactory stressReportFactory,
                                ConsoleStressReportPrinter consoleStressReportPrinter) {
        this.stressHttpClient = stressHttpClient;
        this.stressReportFactory = stressReportFactory;
        this.consoleStressReportPrinter = consoleStressReportPrinter;
    }

    public StressTestReport run(StressTestConfig config, StressExecutionContext executionContext) {
        ExecutorService executorService = Executors.newFixedThreadPool(config.getExecution().getConcurrencyLevel());
        StressMetricsCollector collector = new StressMetricsCollector();
        collector.start();

        try {
            List<Future<RequestExecutionResult>> futures = submitTasks(config, executorService);
            for (Future<RequestExecutionResult> future : futures) {
                collector.record(getResult(future));
            }
        } finally {
            executorService.shutdownNow();
        }

        StressTestReport report = stressReportFactory.create(executionContext.getExecutionId(), config, collector.finish());
        consoleStressReportPrinter.print(report);
        return report;
    }

    private List<Future<RequestExecutionResult>> submitTasks(StressTestConfig config, ExecutorService executorService) {
        List<Future<RequestExecutionResult>> futures = new ArrayList<Future<RequestExecutionResult>>();
        long rampIntervalMillis = calculateRampInterval(config);
        for (int index = 0; index < config.getExecution().getTotalRequests(); index++) {
            futures.add(executorService.submit(new StressHttpTask(stressHttpClient, config)));
            sleepQuietly(rampIntervalMillis);
        }
        return futures;
    }

    private RequestExecutionResult getResult(Future<RequestExecutionResult> future) {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new RequestExecutionResult(false, 0L, exception.getMessage());
        } catch (ExecutionException exception) {
            return new RequestExecutionResult(false, 0L, exception.getCause() == null
                    ? exception.getMessage()
                    : exception.getCause().getMessage());
        }
    }

    private long calculateRampInterval(StressTestConfig config) {
        if (config.getExecution().getRampUpMillis() <= 0) {
            return 0L;
        }
        return config.getExecution().getRampUpMillis() / Math.max(1, config.getExecution().getTotalRequests());
    }

    private void sleepQuietly(long millis) {
        if (millis <= 0L) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
