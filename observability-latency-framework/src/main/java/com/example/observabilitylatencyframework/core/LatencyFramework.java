package com.example.observabilitylatencyframework.core;

import com.example.observabilitylatencyframework.metric.LatencyMetric;

import java.util.List;
import java.util.function.Supplier;

public interface LatencyFramework {

    void record(String operationName, long latencyMs);

    <T> T measure(String operationName, Supplier<T> supplier);

    void measure(String operationName, Runnable runnable);

    LatencyMetric getMetric(String operationName);

    List<LatencyMetric> getAllMetrics();

    void reset(String operationName);

    void resetAll();
}

