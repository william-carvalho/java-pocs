package com.example.unusedclassdetector.scanner;

import com.example.unusedclassdetector.config.DetectorConfig;
import com.example.unusedclassdetector.exception.DetectorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SourceFileScanner {

    public List<Path> scan(DetectorConfig config, List<String> warnings) {
        if (config.getSourceRoot() == null || !Files.exists(config.getSourceRoot())) {
            throw new DetectorException("Source root does not exist: " + config.getSourceRoot());
        }

        List<Path> javaFiles = new ArrayList<Path>();
        try (Stream<Path> stream = Files.walk(config.getSourceRoot())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !shouldIgnorePath(path, config))
                    .forEach(javaFiles::add);
        } catch (IOException exception) {
            throw new DetectorException("Could not scan source root: " + config.getSourceRoot(), exception);
        }

        if (javaFiles.isEmpty()) {
            warnings.add("No Java files were found in the source root");
        }
        return javaFiles;
    }

    private boolean shouldIgnorePath(Path path, DetectorConfig config) {
        String normalized = path.toString().replace("\\", "/");
        if (normalized.contains("/target/") || normalized.contains("/build/") || normalized.contains("/.git/")) {
            return true;
        }
        return !config.isIncludeTests() && normalized.contains("/src/test/");
    }
}

