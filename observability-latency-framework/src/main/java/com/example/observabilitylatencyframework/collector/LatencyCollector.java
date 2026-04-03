package com.example.observabilitylatencyframework.collector;

import com.example.observabilitylatencyframework.registry.LatencyRegistry;

public class LatencyCollector {

    private final LatencyRegistry latencyRegistry;

    public LatencyCollector(LatencyRegistry latencyRegistry) {
        this.latencyRegistry = latencyRegistry;
    }

    public void record(String operationName, long latencyMs) {
        record(operationName, latencyMs, false);
    }

    public void record(String operationName, long latencyMs, boolean error) {
        latencyRegistry.getOrCreate(operationName).record(latencyMs, error);
    }
}

