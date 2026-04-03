package com.example.hibernateslowquerydetector.util;

public final class H2DatabaseFunctions {

    private H2DatabaseFunctions() {
    }

    public static int sleepMs(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
        return milliseconds;
    }
}
