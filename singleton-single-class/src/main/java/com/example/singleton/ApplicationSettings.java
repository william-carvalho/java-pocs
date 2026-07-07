package com.example.singleton;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApplicationSettings {
    private final Map<String, String> values = new LinkedHashMap<String, String>();

    private ApplicationSettings() {
        values.put("application.name", "singleton-single-class");
        values.put("environment", "local");
    }

    public static ApplicationSettings getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized void set(String key, String value) {
        validateKey(key);
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
        values.put(key, value);
    }

    public synchronized String get(String key) {
        validateKey(key);
        return values.get(key);
    }

    public synchronized boolean contains(String key) {
        validateKey(key);
        return values.containsKey(key);
    }

    public synchronized String remove(String key) {
        validateKey(key);
        return values.remove(key);
    }

    public synchronized Map<String, String> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(values));
    }

    synchronized void resetForTests() {
        values.clear();
        values.put("application.name", "singleton-single-class");
        values.put("environment", "local");
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key is required");
        }
    }

    private static final class Holder {
        private static final ApplicationSettings INSTANCE = new ApplicationSettings();

        private Holder() {
        }
    }
}
