package com.example.yamlcodegenerator.util;

public final class NameUtils {

    private NameUtils() {
    }

    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}

