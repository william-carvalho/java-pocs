package com.example.unusedclass;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UnusedClassDetector {
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("\\bpackage\\s+([a-zA-Z0-9_.]+)\\s*;");
    private static final Pattern TYPE_PATTERN = Pattern.compile("\\b(class|interface|enum)\\s+([A-Za-z_$][A-Za-z0-9_$]*)\\b");

    public AnalysisResult detect(DetectorConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is required");
        }
        List<JavaSourceFile> sourceFiles = scan(config.getSourceRoot(), config.isIncludeTests());
        List<ProjectClass> classes = new ArrayList<ProjectClass>();
        for (JavaSourceFile sourceFile : sourceFiles) {
            ProjectClass parsed = parse(sourceFile);
            if (parsed != null) {
                classes.add(parsed);
            }
        }

        Map<String, Set<String>> references = buildReferenceGraph(classes);
        List<ProjectClass> ignored = new ArrayList<ProjectClass>();
        List<ProjectClass> unused = new ArrayList<ProjectClass>();
        for (ProjectClass projectClass : classes) {
            if (isRoot(projectClass, config)) {
                ignored.add(projectClass);
                continue;
            }
            int incoming = incomingReferenceCount(projectClass, references);
            if (incoming == 0) {
                unused.add(projectClass);
            }
        }
        return new AnalysisResult(config.getSourceRoot(), classes, references, ignored, unused);
    }

    public static DetectorConfig config(Path sourceRoot) {
        return new DetectorConfig(sourceRoot, Collections.<String>emptyList(), false);
    }

    public List<JavaSourceFile> scan(Path sourceRoot, boolean includeTests) {
        if (sourceRoot == null || !Files.exists(sourceRoot)) {
            throw new IllegalArgumentException("sourceRoot must exist");
        }
        final List<JavaSourceFile> files = new ArrayList<JavaSourceFile>();
        try {
            Files.walk(sourceRoot)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> includeTests || !path.toString().contains("\\test\\") && !path.toString().contains("/test/"))
                    .forEach(path -> {
                        try {
                            files.add(new JavaSourceFile(path, new String(Files.readAllBytes(path), StandardCharsets.UTF_8)));
                        } catch (IOException ex) {
                            throw new IllegalStateException("Could not read " + path, ex);
                        }
                    });
        } catch (IOException ex) {
            throw new IllegalStateException("Could not scan " + sourceRoot, ex);
        }
        return Collections.unmodifiableList(files);
    }

    public ProjectClass parse(JavaSourceFile sourceFile) {
        String sanitized = sanitize(sourceFile.getSource());
        Matcher typeMatcher = TYPE_PATTERN.matcher(sanitized);
        if (!typeMatcher.find()) {
            return null;
        }

        Matcher packageMatcher = PACKAGE_PATTERN.matcher(sanitized);
        String packageName = packageMatcher.find() ? packageMatcher.group(1) : "";
        String simpleName = typeMatcher.group(2);
        return new ProjectClass(
                sourceFile.getPath(),
                packageName,
                simpleName,
                packageName.isEmpty() ? simpleName : packageName + "." + simpleName,
                sanitized);
    }

    private Map<String, Set<String>> buildReferenceGraph(List<ProjectClass> classes) {
        Map<String, Set<String>> graph = new LinkedHashMap<String, Set<String>>();
        for (ProjectClass source : classes) {
            Set<String> outbound = new LinkedHashSet<String>();
            for (ProjectClass target : classes) {
                if (source.getQualifiedName().equals(target.getQualifiedName())) {
                    continue;
                }
                if (referencesClass(source.getSanitizedSource(), target)) {
                    outbound.add(target.getQualifiedName());
                }
            }
            graph.put(source.getQualifiedName(), outbound);
        }
        return graph;
    }

    private static boolean referencesClass(String source, ProjectClass target) {
        Pattern simpleNamePattern = Pattern.compile("(?<![A-Za-z0-9_$])" + Pattern.quote(target.getSimpleName()) + "(?![A-Za-z0-9_$])");
        Pattern qualifiedNamePattern = Pattern.compile("(?<![A-Za-z0-9_$.])" + Pattern.quote(target.getQualifiedName()) + "(?![A-Za-z0-9_$.])");
        return simpleNamePattern.matcher(source).find() || qualifiedNamePattern.matcher(source).find();
    }

    private static int incomingReferenceCount(ProjectClass target, Map<String, Set<String>> references) {
        int count = 0;
        for (Set<String> outbound : references.values()) {
            if (outbound.contains(target.getQualifiedName())) {
                count++;
            }
        }
        return count;
    }

    private static boolean isRoot(ProjectClass projectClass, DetectorConfig config) {
        String simpleName = projectClass.getSimpleName();
        String packageName = projectClass.getPackageName();
        if (projectClass.getSanitizedSource().contains("public static void main")) {
            return true;
        }
        if (simpleName.endsWith("Application") || simpleName.endsWith("Main") || simpleName.endsWith("Config")) {
            return true;
        }
        if (packageName.endsWith(".config") || packageName.equals("config") || packageName.contains(".config.")) {
            return true;
        }
        for (String ignorePattern : config.getIgnorePatterns()) {
            if (projectClass.getQualifiedName().matches(ignorePattern) || simpleName.matches(ignorePattern)) {
                return true;
            }
        }
        return false;
    }

    private static String sanitize(String source) {
        String withoutBlockComments = source.replaceAll("(?s)/\\*.*?\\*/", " ");
        String withoutLineComments = withoutBlockComments.replaceAll("(?m)//.*$", " ");
        return withoutLineComments.replaceAll("\"(?:\\\\.|[^\"\\\\])*\"", "\"\"");
    }

    public static final class DetectorConfig {
        private final Path sourceRoot;
        private final List<String> ignorePatterns;
        private final boolean includeTests;

        public DetectorConfig(Path sourceRoot, List<String> ignorePatterns, boolean includeTests) {
            if (sourceRoot == null) {
                throw new IllegalArgumentException("sourceRoot is required");
            }
            this.sourceRoot = sourceRoot;
            this.ignorePatterns = ignorePatterns == null
                    ? Collections.<String>emptyList()
                    : Collections.unmodifiableList(new ArrayList<String>(ignorePatterns));
            this.includeTests = includeTests;
        }

        public Path getSourceRoot() {
            return sourceRoot;
        }

        public List<String> getIgnorePatterns() {
            return ignorePatterns;
        }

        public boolean isIncludeTests() {
            return includeTests;
        }
    }

    public static final class JavaSourceFile {
        private final Path path;
        private final String source;

        public JavaSourceFile(Path path, String source) {
            this.path = path;
            this.source = source;
        }

        public Path getPath() {
            return path;
        }

        public String getSource() {
            return source;
        }
    }

    public static final class ProjectClass {
        private final Path path;
        private final String packageName;
        private final String simpleName;
        private final String qualifiedName;
        private final String sanitizedSource;

        private ProjectClass(Path path, String packageName, String simpleName, String qualifiedName, String sanitizedSource) {
            this.path = path;
            this.packageName = packageName;
            this.simpleName = simpleName;
            this.qualifiedName = qualifiedName;
            this.sanitizedSource = sanitizedSource;
        }

        public Path getPath() {
            return path;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }

        public String getSanitizedSource() {
            return sanitizedSource;
        }
    }

    public static final class AnalysisResult {
        private final Path sourceRoot;
        private final List<ProjectClass> classes;
        private final Map<String, Set<String>> references;
        private final List<ProjectClass> ignoredClasses;
        private final List<ProjectClass> potentiallyUnusedClasses;

        private AnalysisResult(Path sourceRoot,
                               List<ProjectClass> classes,
                               Map<String, Set<String>> references,
                               List<ProjectClass> ignoredClasses,
                               List<ProjectClass> potentiallyUnusedClasses) {
            this.sourceRoot = sourceRoot;
            this.classes = Collections.unmodifiableList(new ArrayList<ProjectClass>(classes));
            this.references = immutableGraph(references);
            this.ignoredClasses = Collections.unmodifiableList(new ArrayList<ProjectClass>(ignoredClasses));
            this.potentiallyUnusedClasses = Collections.unmodifiableList(new ArrayList<ProjectClass>(potentiallyUnusedClasses));
        }

        private static Map<String, Set<String>> immutableGraph(Map<String, Set<String>> references) {
            Map<String, Set<String>> copy = new LinkedHashMap<String, Set<String>>();
            for (Map.Entry<String, Set<String>> entry : references.entrySet()) {
                copy.put(entry.getKey(), Collections.unmodifiableSet(new LinkedHashSet<String>(entry.getValue())));
            }
            return Collections.unmodifiableMap(copy);
        }

        public String toTextReport() {
            StringBuilder report = new StringBuilder();
            report.append("Unused Class Detector Report\n");
            report.append("Source root: ").append(sourceRoot).append("\n");
            report.append("Total classes found: ").append(classes.size()).append("\n");
            report.append("Ignored classes: ").append(ignoredClasses.size()).append("\n");
            report.append("Potentially unused classes: ").append(potentiallyUnusedClasses.size()).append("\n");
            for (ProjectClass projectClass : potentiallyUnusedClasses) {
                report.append("- ").append(projectClass.getQualifiedName()).append("\n");
            }
            return report.toString();
        }

        public Path getSourceRoot() {
            return sourceRoot;
        }

        public List<ProjectClass> getClasses() {
            return classes;
        }

        public Map<String, Set<String>> getReferences() {
            return references;
        }

        public List<ProjectClass> getIgnoredClasses() {
            return ignoredClasses;
        }

        public List<ProjectClass> getPotentiallyUnusedClasses() {
            return potentiallyUnusedClasses;
        }
    }
}
