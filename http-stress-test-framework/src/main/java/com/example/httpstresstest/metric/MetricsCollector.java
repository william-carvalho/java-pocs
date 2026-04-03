package com.example.httpstresstest.metric;

import com.example.httpstresstest.model.RequestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetricsCollector {

    private final List<RequestResult> results = new ArrayList<RequestResult>();

    public void add(RequestResult result) {
        results.add(result);
    }

    public int getSuccessfulRequests() {
        int count = 0;
        for (RequestResult result : results) {
            if (result.isSuccess()) {
                count++;
            }
        }
        return count;
    }

    public int getFailedRequests() {
        return results.size() - getSuccessfulRequests();
    }

    public Map<Integer, Integer> getStatusCodeCounts() {
        Map<Integer, Integer> counts = new TreeMap<Integer, Integer>();
        for (RequestResult result : results) {
            Integer current = counts.get(result.getStatusCode());
            counts.put(result.getStatusCode(), current == null ? 1 : current + 1);
        }
        return counts;
    }

    public LatencyStats calculateLatencyStats() {
        if (results.isEmpty()) {
            return new LatencyStats(0, 0, 0.0d, 0, 0);
        }

        List<Long> latencies = new ArrayList<Long>();
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long total = 0L;

        for (RequestResult result : results) {
            long latency = result.getLatencyMs();
            latencies.add(latency);
            min = Math.min(min, latency);
            max = Math.max(max, latency);
            total += latency;
        }

        Collections.sort(latencies);
        double average = (double) total / latencies.size();
        long p50 = percentile(latencies, 0.50d);
        long p95 = percentile(latencies, 0.95d);

        return new LatencyStats(min, max, average, p50, p95);
    }

    public List<RequestResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    private long percentile(List<Long> sortedLatencies, double percentile) {
        int index = (int) Math.ceil(percentile * sortedLatencies.size()) - 1;
        index = Math.max(0, Math.min(index, sortedLatencies.size() - 1));
        return sortedLatencies.get(index);
    }
}

