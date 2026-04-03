package com.example.unusedclassdetector.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetectorConfig {

    private final Path sourceRoot;
    private final List<String> ignorePatterns;
    private final boolean includeTests;

    public DetectorConfig(Path sourceRoot, List<String> ignorePatterns, boolean includeTests) {
        this.sourceRoot = sourceRoot;
        this.ignorePatterns = ignorePatterns == null ? new ArrayList<String>() : new ArrayList<String>(ignorePatterns);
        this.includeTests = includeTests;
    }

    public Path getSourceRoot() {
        return sourceRoot;
    }

    public List<String> getIgnorePatterns() {
        return Collections.unmodifiableList(ignorePatterns);
    }

    public boolean isIncludeTests() {
        return includeTests;
    }
}

