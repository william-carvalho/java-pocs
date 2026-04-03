package com.example.unusedclassdetector.analyzer;

import com.example.unusedclassdetector.config.DetectorConfig;
import com.example.unusedclassdetector.model.AnalysisResult;
import com.example.unusedclassdetector.model.ClassReferenceGraph;
import com.example.unusedclassdetector.model.JavaSourceFile;
import com.example.unusedclassdetector.model.ProjectClassInfo;
import com.example.unusedclassdetector.parser.JavaSourceParser;
import com.example.unusedclassdetector.scanner.SourceFileScanner;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UnusedClassDetector {

    private final SourceFileScanner sourceFileScanner;
    private final JavaSourceParser javaSourceParser;
    private final DependencyAnalyzer dependencyAnalyzer;

    public UnusedClassDetector() {
        this(new SourceFileScanner(), new JavaSourceParser(), new DependencyAnalyzer());
    }

    public UnusedClassDetector(SourceFileScanner sourceFileScanner,
                               JavaSourceParser javaSourceParser,
                               DependencyAnalyzer dependencyAnalyzer) {
        this.sourceFileScanner = sourceFileScanner;
        this.javaSourceParser = javaSourceParser;
        this.dependencyAnalyzer = dependencyAnalyzer;
    }

    public AnalysisResult detect(DetectorConfig config) {
        List<String> warnings = new ArrayList<String>();
        List<Path> javaFiles = sourceFileScanner.scan(config, warnings);
        List<JavaSourceFile> sourceFiles = new ArrayList<JavaSourceFile>();

        for (Path javaFile : javaFiles) {
            try {
                sourceFiles.add(javaSourceParser.parse(javaFile));
            } catch (RuntimeException exception) {
                warnings.add("Could not parse file " + javaFile + ": " + exception.getMessage());
            }
        }

        List<ProjectClassInfo> classIndex = dependencyAnalyzer.buildClassIndex(sourceFiles, config);
        ClassReferenceGraph graph = dependencyAnalyzer.analyze(sourceFiles, classIndex);

        List<String> ignoredClasses = classIndex.stream()
                .filter(ProjectClassInfo::isRootClass)
                .map(ProjectClassInfo::getFullClassName)
                .sorted()
                .collect(Collectors.toList());

        List<String> potentiallyUnused = classIndex.stream()
                .filter(classInfo -> !classInfo.isRootClass())
                .filter(classInfo -> graph.getInboundReferenceCounts().get(classInfo.getFullClassName()) == 0)
                .map(ProjectClassInfo::getFullClassName)
                .sorted()
                .collect(Collectors.toList());

        warnings.add("Potentially unused means heuristic-only analysis based on source scanning");
        warnings.add("Reflection, dynamic loading and framework wiring are not fully analyzed");

        int referencedClasses = (int) graph.getInboundReferenceCounts().values().stream()
                .filter(count -> count > 0)
                .count();

        return new AnalysisResult(
                config.getSourceRoot(),
                classIndex.size(),
                referencedClasses,
                ignoredClasses,
                potentiallyUnused,
                Collections.unmodifiableList(warnings)
        );
    }
}

