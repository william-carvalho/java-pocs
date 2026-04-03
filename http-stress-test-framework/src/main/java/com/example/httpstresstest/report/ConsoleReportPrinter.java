package com.example.httpstresstest.report;

import java.util.Map;

public final class ConsoleReportPrinter {

    private ConsoleReportPrinter() {
    }

    public static void print(StressTestReport report) {
        System.out.println("Stress Test Report");
        System.out.println("-------------------");
        System.out.println("URL: " + report.getUrl());
        System.out.println("Method: " + report.getMethod());
        System.out.println("Total Requests: " + report.getTotalRequests());
        System.out.println("Successful Requests: " + report.getSuccessfulRequests());
        System.out.println("Failed Requests: " + report.getFailedRequests());
        System.out.println("Success Rate: " + String.format("%.2f%%", report.getSuccessRate()));
        System.out.println("Min Latency: " + report.getMinLatencyMs() + " ms");
        System.out.println("Max Latency: " + report.getMaxLatencyMs() + " ms");
        System.out.println("Avg Latency: " + String.format("%.2f", report.getAvgLatencyMs()) + " ms");
        System.out.println("P50 Latency: " + report.getP50LatencyMs() + " ms");
        System.out.println("P95 Latency: " + report.getP95LatencyMs() + " ms");
        System.out.println("Total Duration: " + report.getTotalDurationMs() + " ms");
        System.out.println("Requests/sec: " + String.format("%.2f", report.getRequestsPerSecond()));
        System.out.println("Status Codes:");
        for (Map.Entry<Integer, Integer> entry : report.getStatusCodeCounts().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

