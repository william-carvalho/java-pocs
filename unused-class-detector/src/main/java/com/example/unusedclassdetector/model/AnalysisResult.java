package com.example.unusedclassdetector.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class AnalysisResult {

    private final Path sourceRoot;
    private final int totalClasses;
    private final int referencedClasses;
    private final List<String> ignoredClasses;
    private final List<String> potentiallyUnusedClasses;
    private final List<String> warnings;

    public AnalysisResult(Path sourceRoot,
                          int totalClasses,
                          int referencedClasses,
                          List<String> ignoredClasses,
                          List<String> potentiallyUnusedClasses,
                          List<String> warnings) {
        this.sourceRoot = sourceRoot;
        this.totalClasses = totalClasses;
        this.referencedClasses = referencedClasses;
        this.ignoredClasses = ignoredClasses;
        this.potentiallyUnusedClasses = potentiallyUnusedClasses;
        this.warnings = warnings;
    }

    public Path getSourceRoot() {
        return sourceRoot;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public int getReferencedClasses() {
        return referencedClasses;
    }

    public List<String> getIgnoredClasses() {
        return Collections.unmodifiableList(ignoredClasses);
    }

    public List<String> getPotentiallyUnusedClasses() {
        return Collections.unmodifiableList(potentiallyUnusedClasses);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}

