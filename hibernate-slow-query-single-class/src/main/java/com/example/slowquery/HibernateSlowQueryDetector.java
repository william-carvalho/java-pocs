package com.example.slowquery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public final class HibernateSlowQueryDetector {
    private final long thresholdMs;
    private final Clock clock;
    private final List<SlowQueryRecord> history = new ArrayList<SlowQueryRecord>();
    private long nextId = 1;

    public HibernateSlowQueryDetector(long thresholdMs) {
        this(thresholdMs, new SystemClock());
    }

    public HibernateSlowQueryDetector(long thresholdMs, Clock clock) {
        if (thresholdMs < 0) {
            throw new IllegalArgumentException("thresholdMs must be zero or greater");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock is required");
        }
        this.thresholdMs = thresholdMs;
        this.clock = clock;
    }

    public String inspect(String sql) {
        QueryContext.setLastSql(sql);
        return sql;
    }

    public <T> T monitor(String source, QueryWork<T> work) {
        return monitor(source, new Callable<T>() {
            public T call() throws Exception {
                return work.execute();
            }
        });
    }

    public <T> T monitor(String source, Callable<T> work) {
        if (work == null) {
            throw new IllegalArgumentException("work is required");
        }
        QueryContext.clear();
        long started = clock.nanoTime();
        try {
            return work.call();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new QueryExecutionException("Query execution failed", ex);
        } finally {
            long elapsedMs = Math.max(0L, (clock.nanoTime() - started) / 1000000L);
            String sql = QueryContext.getLastSql();
            if (sql != null && elapsedMs >= thresholdMs) {
                record(sql, elapsedMs, source);
            }
            QueryContext.clear();
        }
    }

    public void record(String sqlText, long executionTimeMs, String source) {
        if (isBlank(sqlText)) {
            throw new IllegalArgumentException("sqlText is required");
        }
        if (executionTimeMs < 0) {
            throw new IllegalArgumentException("executionTimeMs must be zero or greater");
        }
        history.add(new SlowQueryRecord(
                nextId++,
                normalizeSql(sqlText),
                executionTimeMs,
                thresholdMs,
                LocalDateTime.now(),
                detectQueryType(sqlText),
                source == null ? "unknown" : source));
    }

    public List<SlowQueryRecord> listSlowQueries() {
        return Collections.unmodifiableList(new ArrayList<SlowQueryRecord>(history));
    }

    public SlowQueryRecord getSlowQuery(long id) {
        for (SlowQueryRecord record : history) {
            if (record.getId() == id) {
                return record;
            }
        }
        throw new IllegalArgumentException("Slow query not found: " + id);
    }

    public void clearHistory() {
        history.clear();
    }

    public SlowQueryStats stats() {
        if (history.isEmpty()) {
            return new SlowQueryStats(0, 0.0, 0L, 0L);
        }
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long total = 0L;
        for (SlowQueryRecord record : history) {
            min = Math.min(min, record.getExecutionTimeMs());
            max = Math.max(max, record.getExecutionTimeMs());
            total += record.getExecutionTimeMs();
        }
        return new SlowQueryStats(history.size(), total / (double) history.size(), min, max);
    }

    public long getThresholdMs() {
        return thresholdMs;
    }

    public static String detectQueryType(String sql) {
        if (isBlank(sql)) {
            return "UNKNOWN";
        }
        String trimmed = sql.trim();
        int index = 0;
        while (index < trimmed.length() && !Character.isWhitespace(trimmed.charAt(index))) {
            index++;
        }
        return trimmed.substring(0, index).toUpperCase(Locale.ENGLISH);
    }

    private static String normalizeSql(String sql) {
        return sql.trim().replaceAll("\\s+", " ");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface QueryWork<T> {
        T execute();
    }

    public interface Clock {
        long nanoTime();
    }

    private static final class SystemClock implements Clock {
        public long nanoTime() {
            return System.nanoTime();
        }
    }

    private static final class QueryContext {
        private static final ThreadLocal<String> LAST_SQL = new ThreadLocal<String>();

        private static void setLastSql(String sql) {
            LAST_SQL.set(sql);
        }

        private static String getLastSql() {
            return LAST_SQL.get();
        }

        private static void clear() {
            LAST_SQL.remove();
        }
    }

    public static final class SlowQueryRecord {
        private final long id;
        private final String sqlText;
        private final long executionTimeMs;
        private final long thresholdMs;
        private final LocalDateTime detectedAt;
        private final String queryType;
        private final String source;

        private SlowQueryRecord(long id,
                                String sqlText,
                                long executionTimeMs,
                                long thresholdMs,
                                LocalDateTime detectedAt,
                                String queryType,
                                String source) {
            this.id = id;
            this.sqlText = sqlText;
            this.executionTimeMs = executionTimeMs;
            this.thresholdMs = thresholdMs;
            this.detectedAt = detectedAt;
            this.queryType = queryType;
            this.source = source;
        }

        public long getId() {
            return id;
        }

        public String getSqlText() {
            return sqlText;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public long getThresholdMs() {
            return thresholdMs;
        }

        public LocalDateTime getDetectedAt() {
            return detectedAt;
        }

        public String getQueryType() {
            return queryType;
        }

        public String getSource() {
            return source;
        }
    }

    public static final class SlowQueryStats {
        private final int totalDetected;
        private final double averageExecutionTimeMs;
        private final long minExecutionTimeMs;
        private final long maxExecutionTimeMs;

        private SlowQueryStats(int totalDetected, double averageExecutionTimeMs, long minExecutionTimeMs, long maxExecutionTimeMs) {
            this.totalDetected = totalDetected;
            this.averageExecutionTimeMs = averageExecutionTimeMs;
            this.minExecutionTimeMs = minExecutionTimeMs;
            this.maxExecutionTimeMs = maxExecutionTimeMs;
        }

        public int getTotalDetected() {
            return totalDetected;
        }

        public double getAverageExecutionTimeMs() {
            return averageExecutionTimeMs;
        }

        public long getMinExecutionTimeMs() {
            return minExecutionTimeMs;
        }

        public long getMaxExecutionTimeMs() {
            return maxExecutionTimeMs;
        }
    }

    public static final class QueryExecutionException extends RuntimeException {
        private QueryExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
