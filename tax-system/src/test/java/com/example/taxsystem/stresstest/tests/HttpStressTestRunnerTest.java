package com.example.taxsystem.stresstest.tests;

import com.example.taxsystem.common.config.StressExecutionContext;
import com.example.taxsystem.stresstest.domain.StressExecutionDefinition;
import com.example.taxsystem.stresstest.domain.StressExecutionMode;
import com.example.taxsystem.stresstest.domain.StressTargetDefinition;
import com.example.taxsystem.stresstest.domain.StressTestConfig;
import com.example.taxsystem.stresstest.domain.StressTestReport;
import com.example.taxsystem.stresstest.infrastructure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpStressTestRunnerTest {

    private final StressHttpClient stressHttpClient = mock(StressHttpClient.class);

    @Spy
    private StressReportFactory stressReportFactory = new StressReportFactory();

    private final ConsoleStressReportPrinter consoleStressReportPrinter = mock(ConsoleStressReportPrinter.class);

    @Test
    void shouldAggregateStressResultsAndPrintReport() {
        HttpStressTestRunner runner = new HttpStressTestRunner(stressHttpClient, stressReportFactory, consoleStressReportPrinter);
        StressTestConfig config = buildConfig();
        AtomicInteger counter = new AtomicInteger();

        when(stressHttpClient.execute(ArgumentMatchers.any(StressTestConfig.class))).thenAnswer(invocation -> {
            int index = counter.incrementAndGet();
            if (index == 3) {
                return new RequestExecutionResult(false, 40L, "500 INTERNAL_SERVER_ERROR");
            }
            return new RequestExecutionResult(true, 20L * index, "200 OK");
        });

        StressTestReport report = runner.run(config, new StressExecutionContext());

        verify(stressReportFactory).create(anyString(), any(StressTestConfig.class), any(StressMetricsCollector.Snapshot.class));
        verify(consoleStressReportPrinter).print(any(StressTestReport.class));
        assertThat(report.getTotalRequests()).isEqualTo(3);
        assertThat(report.getSuccessCount()).isEqualTo(2);
        assertThat(report.getErrorCount()).isEqualTo(1);
        assertThat(report.getP95LatencyMillis()).isGreaterThanOrEqualTo(report.getP50LatencyMillis());
    }

    private StressTestConfig buildConfig() {
        StressTargetDefinition target = new StressTargetDefinition();
        target.setUrl("https://example.org");
        target.setMethod(HttpMethod.GET);

        StressExecutionDefinition execution = new StressExecutionDefinition();
        execution.setTotalRequests(3);
        execution.setConcurrencyLevel(2);
        execution.setTimeoutMillis(500);
        execution.setRampUpMillis(0);
        execution.setMode(StressExecutionMode.SYNC);

        StressTestConfig config = new StressTestConfig();
        config.setTarget(target);
        config.setExecution(execution);
        return config;
    }
}
