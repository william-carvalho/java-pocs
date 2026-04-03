package com.example.observabilitylatencyframework.timer;

public final class OperationTimer {

    private OperationTimer() {
    }

    public static long start() {
        return System.nanoTime();
    }

    public static long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000L;
    }
}

