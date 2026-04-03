package com.example.observabilitylatencyframework.util;

import com.example.observabilitylatencyframework.metric.LatencyMetric;

import java.util.List;

public final class MetricPrinter {

    private MetricPrinter() {
    }

    public static void printMetric(LatencyMetric metric) {
        System.out.println("Operation: " + metric.getOperationName());
        System.out.println("count: " + metric.getCount());
        System.out.println("minLatencyMs: " + metric.getMinLatencyMs());
        System.out.println("maxLatencyMs: " + metric.getMaxLatencyMs());
        System.out.println("avgLatencyMs: " + metric.getAvgLatencyMs());
        System.out.println("totalLatencyMs: " + metric.getTotalLatencyMs());
        System.out.println("lastLatencyMs: " + metric.getLastLatencyMs());
        System.out.println("errorCount: " + metric.getErrorCount());
    }

    public static void printAll(List<LatencyMetric> metrics) {
        for (LatencyMetric metric : metrics) {
            printMetric(metric);
            System.out.println();
        }
    }
}

