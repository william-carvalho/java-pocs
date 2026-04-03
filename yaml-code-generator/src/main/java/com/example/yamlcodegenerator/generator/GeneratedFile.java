package com.example.yamlcodegenerator.generator;

import java.nio.file.Path;

public class GeneratedFile {

    private final String className;
    private final Path outputPath;

    public GeneratedFile(String className, Path outputPath) {
        this.className = className;
        this.outputPath = outputPath;
    }

    public String getClassName() {
        return className;
    }

    public Path getOutputPath() {
        return outputPath;
    }
}

