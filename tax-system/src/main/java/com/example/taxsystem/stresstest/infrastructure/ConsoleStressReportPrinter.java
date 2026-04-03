package com.example.taxsystem.stresstest.infrastructure;

import com.example.taxsystem.common.util.JsonUtils;
import com.example.taxsystem.stresstest.domain.StressTestReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsoleStressReportPrinter {

    private static final Logger log = LoggerFactory.getLogger(ConsoleStressReportPrinter.class);

    private final JsonUtils jsonUtils;

    public ConsoleStressReportPrinter(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void print(StressTestReport report) {
        log.info("Stress test {} finished: total={}, success={}, errors={}, avg={}ms, throughput={}/s",
                report.getExecutionId(),
                report.getTotalRequests(),
                report.getSuccessCount(),
                report.getErrorCount(),
                report.getAverageLatencyMillis(),
                String.format("%.2f", report.getThroughputPerSecond()));
        log.info("Stress test JSON report:\n{}", jsonUtils.toJson(report));
    }
}
