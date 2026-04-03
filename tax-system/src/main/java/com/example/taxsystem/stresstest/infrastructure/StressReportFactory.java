package com.example.taxsystem.stresstest.infrastructure;

import com.example.taxsystem.stresstest.domain.StressTestConfig;
import com.example.taxsystem.stresstest.domain.StressTestReport;
import org.springframework.stereotype.Component;

@Component
public class StressReportFactory {

    public StressTestReport create(String executionId, StressTestConfig config, StressMetricsCollector.Snapshot snapshot) {
        int totalRequests = snapshot.getSuccessCount() + snapshot.getErrorCount();
        double throughput = snapshot.getDurationMillis() == 0L
                ? 0.0d
                : (totalRequests * 1000.0d) / snapshot.getDurationMillis();

        return StressTestReport.builder()
                .executionId(executionId)
                .url(config.getTarget().getUrl())
                .method(config.getTarget().getMethod())
                .mode(config.getExecution().getMode())
                .totalRequests(totalRequests)
                .successCount(snapshot.getSuccessCount())
                .errorCount(snapshot.getErrorCount())
                .averageLatencyMillis(snapshot.getAverageLatencyMillis())
                .minLatencyMillis(snapshot.getMinLatencyMillis())
                .maxLatencyMillis(snapshot.getMaxLatencyMillis())
                .p50LatencyMillis(snapshot.getP50LatencyMillis())
                .p95LatencyMillis(snapshot.getP95LatencyMillis())
                .throughputPerSecond(throughput)
                .durationMillis(snapshot.getDurationMillis())
                .startedAt(snapshot.getStartedAt())
                .completedAt(snapshot.getCompletedAt())
                .sampleErrors(snapshot.getSampleErrors())
                .build();
    }
}
