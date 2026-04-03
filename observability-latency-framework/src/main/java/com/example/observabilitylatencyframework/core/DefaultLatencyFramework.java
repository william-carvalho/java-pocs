package com.example.observabilitylatencyframework.core;

import com.example.observabilitylatencyframework.collector.LatencyCollector;
import com.example.observabilitylatencyframework.exception.InvalidMetricException;
import com.example.observabilitylatencyframework.exception.MetricNotFoundException;
import com.example.observabilitylatencyframework.metric.LatencyMetric;
import com.example.observabilitylatencyframework.registry.LatencyRegistry;
import com.example.observabilitylatencyframework.timer.OperationTimer;

import java.util.List;
import java.util.function.Supplier;

public class DefaultLatencyFramework implements LatencyFramework {

    private final LatencyRegistry latencyRegistry;
    private final LatencyCollector latencyCollector;

    public DefaultLatencyFramework() {
        this(new LatencyRegistry());
    }

    public DefaultLatencyFramework(LatencyRegistry latencyRegistry) {
        this.latencyRegistry = latencyRegistry;
        this.latencyCollector = new LatencyCollector(latencyRegistry);
    }

    @Override
    public void record(String operationName, long latencyMs) {
        validateOperationName(operationName);
        validateLatency(latencyMs);
        latencyCollector.record(operationName, latencyMs);
    }

    @Override
    public <T> T measure(String operationName, Supplier<T> supplier) {
        validateOperationName(operationName);
        if (supplier == null) {
            throw new InvalidMetricException("Supplier must not be null");
        }

        long start = OperationTimer.start();
        try {
            T result = supplier.get();
            latencyCollector.record(operationName, OperationTimer.elapsedMillis(start));
            return result;
        } catch (RuntimeException exception) {
            latencyCollector.record(operationName, OperationTimer.elapsedMillis(start), true);
            throw exception;
        }
    }

    @Override
    public void measure(String operationName, Runnable runnable) {
        validateOperationName(operationName);
        if (runnable == null) {
            throw new InvalidMetricException("Runnable must not be null");
        }

        long start = OperationTimer.start();
        try {
            runnable.run();
            latencyCollector.record(operationName, OperationTimer.elapsedMillis(start));
        } catch (RuntimeException exception) {
            latencyCollector.record(operationName, OperationTimer.elapsedMillis(start), true);
            throw exception;
        }
    }

    @Override
    public LatencyMetric getMetric(String operationName) {
        validateOperationName(operationName);
        LatencyMetric metric = latencyRegistry.getMetric(operationName);
        if (metric == null) {
            throw new MetricNotFoundException(operationName);
        }
        return metric;
    }

    @Override
    public List<LatencyMetric> getAllMetrics() {
        return latencyRegistry.getAllMetrics();
    }

    @Override
    public void reset(String operationName) {
        validateOperationName(operationName);
        boolean removed = latencyRegistry.remove(operationName);
        if (!removed) {
            throw new MetricNotFoundException(operationName);
        }
    }

    @Override
    public void resetAll() {
        latencyRegistry.clear();
    }

    private void validateOperationName(String operationName) {
        if (operationName == null || operationName.trim().isEmpty()) {
            throw new InvalidMetricException("Operation name must not be blank");
        }
    }

    private void validateLatency(long latencyMs) {
        if (latencyMs < 0) {
            throw new InvalidMetricException("Latency must be greater than or equal to zero");
        }
    }
}

