package com.example.yamlcodegenerator.writer;

import com.example.yamlcodegenerator.exception.FileGenerationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOutputWriter {

    public Path write(Path outputFile, String content) {
        try {
            Path parent = outputFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            return Files.write(outputFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new FileGenerationException("Could not write generated file: " + outputFile, exception);
        }
    }
}

