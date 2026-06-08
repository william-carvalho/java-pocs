package com.example.observabilitylatencysingleclass;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservabilityLatencySingleClassApplicationTest {

    @Test
    void recordsLatencyMetricsPerOperation() {
        ObservabilityLatencySingleClassApplication.LatencyFramework framework =
                new ObservabilityLatencySingleClassApplication.LatencyFramework();

        framework.record("checkout", 100);
        ObservabilityLatencySingleClassApplication.LatencySummary summary = framework.record("checkout", 300);

        assertThat(summary.operation).isEqualTo("checkout");
        assertThat(summary.count).isEqualTo(2);
        assertThat(summary.averageMillis).isEqualTo(200);
        assertThat(summary.minMillis).isEqualTo(100);
        assertThat(summary.maxMillis).isEqualTo(300);
        assertThat(summary.totalMillis).isEqualTo(400);
        assertThat(summary.lastRecordedAt).isNotNull();
    }

    @Test
    void keepsMetricsSeparatedByOperation() {
        ObservabilityLatencySingleClassApplication.LatencyFramework framework =
                new ObservabilityLatencySingleClassApplication.LatencyFramework();

        framework.record("checkout", 100);
        framework.record("search", 20);

        assertThat(framework.summary("checkout").averageMillis).isEqualTo(100);
        assertThat(framework.summary("search").averageMillis).isEqualTo(20);
    }

    @Test
    void observesCallableLatency() throws Exception {
        ObservabilityLatencySingleClassApplication.LatencyFramework framework =
                new ObservabilityLatencySingleClassApplication.LatencyFramework();

        String result = framework.observe("load-profile", new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(5);
                return "ok";
            }
        });

        assertThat(result).isEqualTo("ok");
        assertThat(framework.summary("load-profile").count).isEqualTo(1);
        assertThat(framework.summary("load-profile").maxMillis).isGreaterThanOrEqualTo(0);
    }

    @Test
    void returnsSortedSummaries() {
        ObservabilityLatencySingleClassApplication.LatencyFramework framework =
                new ObservabilityLatencySingleClassApplication.LatencyFramework();

        framework.record("z-operation", 10);
        framework.record("a-operation", 20);

        List<ObservabilityLatencySingleClassApplication.LatencySummary> summaries = framework.summaries();

        assertThat(summaries).extracting("operation").containsExactly("a-operation", "z-operation");
    }

    @Test
    void reportsHealthFromAverageLatency() {
        ObservabilityLatencySingleClassApplication.LatencyFramework framework =
                new ObservabilityLatencySingleClassApplication.LatencyFramework();

        framework.record("fast", 100);
        framework.record("slow", 900);

        assertThat(framework.isHealthy(500)).isFalse();
        assertThat(framework.isHealthy(1000)).isTrue();
    }

    @Test
    void rejectsInvalidMetrics() {
        ObservabilityLatencySingleClassApplication.LatencyFramework framework =
                new ObservabilityLatencySingleClassApplication.LatencyFramework();

        assertThatThrownBy(() -> framework.record("", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("operation");

        assertThatThrownBy(() -> framework.record("checkout", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non-negative");
    }
}
