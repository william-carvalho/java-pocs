package com.example.socialmediaphotoapp.util;

public final class FileNameUtils {

    private FileNameUtils() {
    }

    public static String extension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot);
    }
}
