package com.example.unusedclassdetector.model;

import java.nio.file.Path;

public class ProjectClassInfo {

    private final String packageName;
    private final String className;
    private final String fullClassName;
    private final Path filePath;
    private final boolean rootClass;

    public ProjectClassInfo(String packageName, String className, String fullClassName, Path filePath, boolean rootClass) {
        this.packageName = packageName;
        this.className = className;
        this.fullClassName = fullClassName;
        this.filePath = filePath;
        this.rootClass = rootClass;
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

    public Path getFilePath() {
        return filePath;
    }

    public boolean isRootClass() {
        return rootClass;
    }
}

