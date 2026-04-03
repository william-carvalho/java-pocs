package com.example.unusedclassdetector.util;

public final class SourceCodeSanitizer {

    private SourceCodeSanitizer() {
    }

    public static String sanitize(String sourceCode) {
        String sanitized = sourceCode.replaceAll("(?s)/\\*.*?\\*/", " ");
        sanitized = sanitized.replaceAll("(?m)//.*$", " ");
        sanitized = sanitized.replaceAll("\"(\\\\.|[^\"\\\\])*\"", "\"\"");
        return sanitized;
    }
}

