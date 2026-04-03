package com.example.observabilitylatencyframework.registry;

import com.example.observabilitylatencyframework.collector.MetricAccumulator;
import com.example.observabilitylatencyframework.metric.LatencyMetric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LatencyRegistry {

    private final ConcurrentHashMap<String, MetricAccumulator> metrics =
            new ConcurrentHashMap<String, MetricAccumulator>();

    public MetricAccumulator getOrCreate(String operationName) {
        return metrics.computeIfAbsent(operationName, ignored -> new MetricAccumulator());
    }

    public LatencyMetric getMetric(String operationName) {
        MetricAccumulator accumulator = metrics.get(operationName);
        return accumulator == null ? null : accumulator.snapshot(operationName);
    }

    public List<LatencyMetric> getAllMetrics() {
        List<LatencyMetric> snapshots = new ArrayList<LatencyMetric>();
        for (Map.Entry<String, MetricAccumulator> entry : metrics.entrySet()) {
            snapshots.add(entry.getValue().snapshot(entry.getKey()));
        }
        Collections.sort(snapshots, Comparator.comparing(LatencyMetric::getOperationName));
        return snapshots;
    }

    public boolean remove(String operationName) {
        return metrics.remove(operationName) != null;
    }

    public void clear() {
        metrics.clear();
    }
}

