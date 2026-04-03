package com.example.unusedclassdetector.analyzer;

import com.example.unusedclassdetector.config.DetectorConfig;
import com.example.unusedclassdetector.model.ClassReferenceGraph;
import com.example.unusedclassdetector.model.JavaSourceFile;
import com.example.unusedclassdetector.model.ProjectClassInfo;
import com.example.unusedclassdetector.util.SourceCodeSanitizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class DependencyAnalyzer {

    public List<ProjectClassInfo> buildClassIndex(List<JavaSourceFile> sourceFiles, DetectorConfig config) {
        List<ProjectClassInfo> classes = new ArrayList<ProjectClassInfo>();
        for (JavaSourceFile sourceFile : sourceFiles) {
            boolean rootClass = isRootClass(sourceFile, config);
            classes.add(new ProjectClassInfo(
                    sourceFile.getPackageName(),
                    sourceFile.getClassName(),
                    sourceFile.getFullClassName(),
                    sourceFile.getFilePath(),
                    rootClass
            ));
        }
        return classes;
    }

    public ClassReferenceGraph analyze(List<JavaSourceFile> sourceFiles, List<ProjectClassInfo> classIndex) {
        Map<String, Set<String>> outbound = new LinkedHashMap<String, Set<String>>();
        Map<String, Integer> inbound = new LinkedHashMap<String, Integer>();

        for (ProjectClassInfo classInfo : classIndex) {
            outbound.put(classInfo.getFullClassName(), new LinkedHashSet<String>());
            inbound.put(classInfo.getFullClassName(), 0);
        }

        for (JavaSourceFile sourceFile : sourceFiles) {
            String sanitizedSource = SourceCodeSanitizer.sanitize(sourceFile.getSourceCode());
            Set<String> dependencies = outbound.get(sourceFile.getFullClassName());

            for (ProjectClassInfo candidate : classIndex) {
                if (candidate.getFullClassName().equals(sourceFile.getFullClassName())) {
                    continue;
                }

                if (referencesCandidate(sanitizedSource, candidate)) {
                    if (dependencies.add(candidate.getFullClassName())) {
                        inbound.put(candidate.getFullClassName(), inbound.get(candidate.getFullClassName()) + 1);
                    }
                }
            }
        }

        return new ClassReferenceGraph(outbound, inbound);
    }

    private boolean referencesCandidate(String sourceCode, ProjectClassInfo candidate) {
        Pattern simpleNamePattern = Pattern.compile("\\b" + Pattern.quote(candidate.getClassName()) + "\\b");
        if (simpleNamePattern.matcher(sourceCode).find()) {
            return true;
        }

        Pattern fullNamePattern = Pattern.compile("\\b" + Pattern.quote(candidate.getFullClassName()) + "\\b");
        return fullNamePattern.matcher(sourceCode).find();
    }

    private boolean isRootClass(JavaSourceFile sourceFile, DetectorConfig config) {
        String className = sourceFile.getClassName();
        String source = sourceFile.getSourceCode();
        String packageName = sourceFile.getPackageName();

        if (className.endsWith("Application") || className.endsWith("Main") || className.endsWith("Config")) {
            return true;
        }
        if (source.contains("public static void main")) {
            return true;
        }
        if (source.contains("@SpringBootApplication")) {
            return true;
        }
        if (packageName.endsWith(".config") || packageName.contains(".config.")) {
            return true;
        }

        for (String ignorePattern : config.getIgnorePatterns()) {
            if (sourceFile.getFullClassName().matches(ignorePattern) || className.matches(ignorePattern)) {
                return true;
            }
        }
        return false;
    }
}

