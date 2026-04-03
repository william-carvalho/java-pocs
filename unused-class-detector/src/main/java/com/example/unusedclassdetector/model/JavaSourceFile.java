package com.example.unusedclassdetector.model;

import java.nio.file.Path;

public class JavaSourceFile {

    private final Path filePath;
    private final String packageName;
    private final String className;
    private final String fullClassName;
    private final String sourceCode;

    public JavaSourceFile(Path filePath, String packageName, String className, String fullClassName, String sourceCode) {
        this.filePath = filePath;
        this.packageName = packageName;
        this.className = className;
        this.fullClassName = fullClassName;
        this.sourceCode = sourceCode;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public String getSourceCode() {
        return sourceCode;
    }
}

