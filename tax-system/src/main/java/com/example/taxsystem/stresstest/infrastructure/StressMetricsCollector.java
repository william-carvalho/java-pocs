package com.example.taxsystem.stresstest.infrastructure;

import com.example.taxsystem.common.util.PercentileCalculator;
import com.google.common.base.Stopwatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BinaryOperator;

public class StressMetricsCollector {

    private final AtomicInteger successCount = new AtomicInteger();
    private final AtomicInteger errorCount = new AtomicInteger();
    private final AtomicLong totalLatency = new AtomicLong();
    private final AtomicLong minLatency = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxLatency = new AtomicLong(0L);
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Long> latencies = new ArrayList<Long>();
    private final List<String> sampleErrors = new ArrayList<String>();
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private final LocalDateTime startedAt = LocalDateTime.now();

    public void start() {
        stopwatch.start();
    }

    public void record(RequestExecutionResult result) {
        if (result.isSuccess()) {
            successCount.incrementAndGet();
        } else {
            errorCount.incrementAndGet();
        }

        totalLatency.addAndGet(result.getLatencyMillis());
        updateAtomic(minLatency, result.getLatencyMillis(), new BinaryOperator<Long>() {
            @Override
            public Long apply(Long left, Long right) {
                return Math.min(left, right);
            }
        });
        updateAtomic(maxLatency, result.getLatencyMillis(), new BinaryOperator<Long>() {
            @Override
            public Long apply(Long left, Long right) {
                return Math.max(left, right);
            }
        });

        lock.lock();
        try {
            latencies.add(result.getLatencyMillis());
            if (!result.isSuccess() && sampleErrors.size() < 5) {
                sampleErrors.add(result.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }

    public Snapshot finish() {
        if (stopwatch.isRunning()) {
            stopwatch.stop();
        }
        long durationMillis = Math.max(1L, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        List<Long> latencySnapshot;
        List<String> errorSnapshot;
        lock.lock();
        try {
            latencySnapshot = new ArrayList<Long>(latencies);
            errorSnapshot = new ArrayList<String>(sampleErrors);
        } finally {
            lock.unlock();
        }

        int total = successCount.get() + errorCount.get();
        long min = total == 0 ? 0L : minLatency.get();
        long max = total == 0 ? 0L : maxLatency.get();
        long average = total == 0 ? 0L : totalLatency.get() / total;

        return new Snapshot(
                startedAt,
                LocalDateTime.now(),
                successCount.get(),
                errorCount.get(),
                average,
                min,
                max,
                PercentileCalculator.percentile(latencySnapshot, 50),
                PercentileCalculator.percentile(latencySnapshot, 95),
                durationMillis,
                errorSnapshot
        );
    }

    private void updateAtomic(AtomicLong holder, long value, BinaryOperator<Long> reducer) {
        long previous;
        long next;
        do {
            previous = holder.get();
            next = reducer.apply(previous, value);
        } while (!holder.compareAndSet(previous, next));
    }

    public static class Snapshot {
        private final LocalDateTime startedAt;
        private final LocalDateTime completedAt;
        private final int successCount;
        private final int errorCount;
        private final long averageLatencyMillis;
        private final long minLatencyMillis;
        private final long maxLatencyMillis;
        private final long p50LatencyMillis;
        private final long p95LatencyMillis;
        private final long durationMillis;
        private final List<String> sampleErrors;

        public Snapshot(LocalDateTime startedAt,
                        LocalDateTime completedAt,
                        int successCount,
                        int errorCount,
                        long averageLatencyMillis,
                        long minLatencyMillis,
                        long maxLatencyMillis,
                        long p50LatencyMillis,
                        long p95LatencyMillis,
                        long durationMillis,
                        List<String> sampleErrors) {
            this.startedAt = startedAt;
            this.completedAt = completedAt;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.averageLatencyMillis = averageLatencyMillis;
            this.minLatencyMillis = minLatencyMillis;
            this.maxLatencyMillis = maxLatencyMillis;
            this.p50LatencyMillis = p50LatencyMillis;
            this.p95LatencyMillis = p95LatencyMillis;
            this.durationMillis = durationMillis;
            this.sampleErrors = sampleErrors;
        }

        public LocalDateTime getStartedAt() {
            return startedAt;
        }

        public LocalDateTime getCompletedAt() {
            return completedAt;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public long getAverageLatencyMillis() {
            return averageLatencyMillis;
        }

        public long getMinLatencyMillis() {
            return minLatencyMillis;
        }

        public long getMaxLatencyMillis() {
            return maxLatencyMillis;
        }

        public long getP50LatencyMillis() {
            return p50LatencyMillis;
        }

        public long getP95LatencyMillis() {
            return p95LatencyMillis;
        }

        public long getDurationMillis() {
            return durationMillis;
        }

        public List<String> getSampleErrors() {
            return sampleErrors;
        }
    }
}
