package com.example.observabilitylatencyframework;

import com.example.observabilitylatencyframework.core.DefaultLatencyFramework;
import com.example.observabilitylatencyframework.core.LatencyFramework;
import com.example.observabilitylatencyframework.exception.InvalidMetricException;
import com.example.observabilitylatencyframework.exception.MetricNotFoundException;
import com.example.observabilitylatencyframework.metric.LatencyMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLatencyFrameworkTest {

    private LatencyFramework framework;

    @BeforeEach
    void setUp() {
        framework = new DefaultLatencyFramework();
    }

    @Test
    void shouldRecordLatencyAndUpdateAggregates() {
        framework.record("db.query.users", 120);
        framework.record("db.query.users", 80);

        LatencyMetric metric = framework.getMetric("db.query.users");

        assertEquals(2, metric.getCount());
        assertEquals(80, metric.getMinLatencyMs());
        assertEquals(120, metric.getMaxLatencyMs());
        assertEquals(200, metric.getTotalLatencyMs());
        assertEquals(80, metric.getLastLatencyMs());
        assertEquals(100.0d, metric.getAvgLatencyMs());
        assertEquals(0, metric.getErrorCount());
    }

    @Test
    void shouldMeasureRunnable() {
        framework.measure("payment.process", () -> sleep(20));

        LatencyMetric metric = framework.getMetric("payment.process");

        assertEquals(1, metric.getCount());
        assertTrue(metric.getLastLatencyMs() >= 0);
    }

    @Test
    void shouldMeasureSupplierAndReturnValue() {
        String result = framework.measure("user-service.findUser", () -> {
            sleep(10);
            return "William";
        });

        LatencyMetric metric = framework.getMetric("user-service.findUser");

        assertEquals("William", result);
        assertEquals(1, metric.getCount());
    }

    @Test
    void shouldRecordLatencyWhenMeasuredOperationFails() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                framework.measure("external.api.call", () -> {
                    sleep(10);
                    throw new RuntimeException("timeout");
                }));

        LatencyMetric metric = framework.getMetric("external.api.call");

        assertEquals("timeout", exception.getMessage());
        assertEquals(1, metric.getCount());
        assertEquals(1, metric.getErrorCount());
        assertTrue(metric.getLastLatencyMs() >= 0);
    }

    @Test
    void shouldResetSingleMetric() {
        framework.record("report.generate", 50);

        framework.reset("report.generate");

        assertThrows(MetricNotFoundException.class, () -> framework.getMetric("report.generate"));
    }

    @Test
    void shouldResetAllMetrics() {
        framework.record("one", 10);
        framework.record("two", 20);

        framework.resetAll();

        assertTrue(framework.getAllMetrics().isEmpty());
    }

    @Test
    void shouldThrowWhenMetricDoesNotExist() {
        assertThrows(MetricNotFoundException.class, () -> framework.getMetric("missing.operation"));
    }

    @Test
    void shouldValidateOperationName() {
        assertThrows(InvalidMetricException.class, () -> framework.record(" ", 10));
    }

    @Test
    void shouldValidateNegativeLatency() {
        assertThrows(InvalidMetricException.class, () -> framework.record("db.query.users", -1));
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }
}

