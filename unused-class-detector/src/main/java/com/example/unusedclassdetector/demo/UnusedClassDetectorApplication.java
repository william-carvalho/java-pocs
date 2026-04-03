package com.example.unusedclassdetector.demo;

import com.example.unusedclassdetector.analyzer.UnusedClassDetector;
import com.example.unusedclassdetector.config.DetectorConfig;
import com.example.unusedclassdetector.model.AnalysisResult;
import com.example.unusedclassdetector.report.UnusedClassReportPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class UnusedClassDetectorApplication {

    public static void main(String[] args) {
        Path sourceRoot = resolveSourceRoot(args);

        DetectorConfig config = new DetectorConfig(
                sourceRoot,
                Arrays.asList(".*Test.*"),
                false
        );

        AnalysisResult result = new UnusedClassDetector().detect(config);
        UnusedClassReportPrinter.print(result);
    }

    private static Path resolveSourceRoot(String[] args) {
        if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            return Paths.get(args[0]);
        }
        return Paths.get("sample-project", "src", "main", "java");
    }
}

