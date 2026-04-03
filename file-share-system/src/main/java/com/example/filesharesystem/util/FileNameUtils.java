package com.example.filesharesystem.util;

public final class FileNameUtils {

    private FileNameUtils() {
    }

    public static String extractExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex);
    }
}
