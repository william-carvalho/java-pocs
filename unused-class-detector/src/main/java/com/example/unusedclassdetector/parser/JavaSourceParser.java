package com.example.unusedclassdetector.parser;

import com.example.unusedclassdetector.exception.DetectorException;
import com.example.unusedclassdetector.model.JavaSourceFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceParser {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([a-zA-Z0-9_.]+)\\s*;");
    private static final Pattern TYPE_PATTERN = Pattern.compile("(class|interface|enum)\\s+([A-Za-z0-9_]+)");

    public JavaSourceFile parse(Path filePath) {
        try {
            String sourceCode = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            return parse(filePath, sourceCode);
        } catch (IOException exception) {
            throw new DetectorException("Could not read Java source file: " + filePath, exception);
        }
    }

    public JavaSourceFile parse(Path filePath, String sourceCode) {
        Matcher packageMatcher = PACKAGE_PATTERN.matcher(sourceCode);
        String packageName = packageMatcher.find() ? packageMatcher.group(1) : "";

        Matcher classMatcher = TYPE_PATTERN.matcher(sourceCode);
        if (!classMatcher.find()) {
            throw new DetectorException("Could not detect top-level type in file: " + filePath);
        }

        String className = classMatcher.group(2);
        String fullClassName = packageName.isEmpty() ? className : packageName + "." + className;
        return new JavaSourceFile(filePath, packageName, className, fullClassName, sourceCode);
    }
}

