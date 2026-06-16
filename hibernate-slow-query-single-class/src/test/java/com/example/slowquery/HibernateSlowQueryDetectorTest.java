package com.example.slowquery;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HibernateSlowQueryDetectorTest {
    @Test
    void fastQueryBelowThresholdIsNotRecorded() {
        ManualClock clock = new ManualClock();
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(200, clock);

        String result = detector.monitor("demo.fast", new HibernateSlowQueryDetector.QueryWork<String>() {
            public String execute() {
                detector.inspect("select * from customers");
                clock.advanceMillis(50);
                return "ok";
            }
        });

        assertEquals("ok", result);
        assertEquals(0, detector.listSlowQueries().size());
    }

    @Test
    void slowQueryAtThresholdIsRecorded() {
        ManualClock clock = new ManualClock();
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(200, clock);

        detector.monitor("demo.slow", new HibernateSlowQueryDetector.QueryWork<Void>() {
            public Void execute() {
                detector.inspect("select sleep_ms(?)");
                clock.advanceMillis(200);
                return null;
            }
        });

        assertEquals(1, detector.listSlowQueries().size());
        HibernateSlowQueryDetector.SlowQueryRecord record = detector.listSlowQueries().get(0);
        assertEquals(1L, record.getId());
        assertEquals("select sleep_ms(?)", record.getSqlText());
        assertEquals(200L, record.getExecutionTimeMs());
        assertEquals(200L, record.getThresholdMs());
        assertEquals("SELECT", record.getQueryType());
        assertEquals("demo.slow", record.getSource());
        assertNotNull(record.getDetectedAt());
    }

    @Test
    void monitorRecordsLastInspectedSqlOnly() {
        ManualClock clock = new ManualClock();
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(10, clock);

        detector.monitor("demo.multiple", new HibernateSlowQueryDetector.QueryWork<Void>() {
            public Void execute() {
                detector.inspect("select * from first_table");
                detector.inspect("update users set name = ?");
                clock.advanceMillis(20);
                return null;
            }
        });

        assertEquals("update users set name = ?", detector.listSlowQueries().get(0).getSqlText());
        assertEquals("UPDATE", detector.listSlowQueries().get(0).getQueryType());
    }

    @Test
    void noSqlCapturedMeansNoRecordEvenWhenSlow() {
        ManualClock clock = new ManualClock();
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(10, clock);

        detector.monitor("demo.no-sql", new HibernateSlowQueryDetector.QueryWork<Void>() {
            public Void execute() {
                clock.advanceMillis(50);
                return null;
            }
        });

        assertEquals(0, detector.listSlowQueries().size());
    }

    @Test
    void statsAreCalculatedFromHistory() {
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(100);
        detector.record("select 1", 100, "one");
        detector.record("select 2", 200, "two");
        detector.record("delete from users", 300, "three");

        HibernateSlowQueryDetector.SlowQueryStats stats = detector.stats();

        assertEquals(3, stats.getTotalDetected());
        assertEquals(200.0, stats.getAverageExecutionTimeMs());
        assertEquals(100L, stats.getMinExecutionTimeMs());
        assertEquals(300L, stats.getMaxExecutionTimeMs());
    }

    @Test
    void emptyStatsReturnZeroes() {
        HibernateSlowQueryDetector.SlowQueryStats stats = new HibernateSlowQueryDetector(100).stats();

        assertEquals(0, stats.getTotalDetected());
        assertEquals(0.0, stats.getAverageExecutionTimeMs());
        assertEquals(0L, stats.getMinExecutionTimeMs());
        assertEquals(0L, stats.getMaxExecutionTimeMs());
    }

    @Test
    void clearHistoryRemovesRecordedSlowQueries() {
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(100);
        detector.record("select 1", 150, "test");

        detector.clearHistory();

        assertEquals(0, detector.listSlowQueries().size());
        assertEquals(0, detector.stats().getTotalDetected());
    }

    @Test
    void getSlowQueryFindsByIdAndFailsClearlyWhenMissing() {
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(100);
        detector.record("select 1", 150, "test");

        assertEquals("select 1", detector.getSlowQuery(1).getSqlText());
        assertThrows(IllegalArgumentException.class, () -> detector.getSlowQuery(99));
    }

    @Test
    void detectsQueryTypes() {
        assertEquals("SELECT", HibernateSlowQueryDetector.detectQueryType(" select * from users"));
        assertEquals("INSERT", HibernateSlowQueryDetector.detectQueryType("insert into users values (?)"));
        assertEquals("UPDATE", HibernateSlowQueryDetector.detectQueryType("update users set name = ?"));
        assertEquals("DELETE", HibernateSlowQueryDetector.detectQueryType("delete from users"));
        assertEquals("UNKNOWN", HibernateSlowQueryDetector.detectQueryType(" "));
    }

    @Test
    void normalizesSqlWhitespaceWhenRecording() {
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(100);

        detector.record(" select   *\nfrom   users ", 150, null);

        HibernateSlowQueryDetector.SlowQueryRecord record = detector.listSlowQueries().get(0);
        assertEquals("select * from users", record.getSqlText());
        assertEquals("unknown", record.getSource());
    }

    @Test
    void wrapsCheckedExceptionsFromQueryWork() {
        HibernateSlowQueryDetector detector = new HibernateSlowQueryDetector(100);

        assertThrows(HibernateSlowQueryDetector.QueryExecutionException.class, () ->
                detector.monitor("demo.error", new Callable<String>() {
                    public String call() throws Exception {
                        throw new Exception("database down");
                    }
                }));
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new HibernateSlowQueryDetector(-1));
        assertThrows(IllegalArgumentException.class, () -> new HibernateSlowQueryDetector(1).record(" ", 1, "x"));
        assertThrows(IllegalArgumentException.class, () -> new HibernateSlowQueryDetector(1).record("select 1", -1, "x"));
        assertThrows(IllegalArgumentException.class, () -> new HibernateSlowQueryDetector(1).monitor("x", (Callable<String>) null));
    }

    private static final class ManualClock implements HibernateSlowQueryDetector.Clock {
        private long nanos;

        public long nanoTime() {
            return nanos;
        }

        private void advanceMillis(long millis) {
            nanos += millis * 1000000L;
        }
    }
}
