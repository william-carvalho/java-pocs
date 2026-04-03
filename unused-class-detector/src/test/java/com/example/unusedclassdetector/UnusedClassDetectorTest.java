package com.example.unusedclassdetector;

import com.example.unusedclassdetector.analyzer.DependencyAnalyzer;
import com.example.unusedclassdetector.analyzer.UnusedClassDetector;
import com.example.unusedclassdetector.config.DetectorConfig;
import com.example.unusedclassdetector.model.AnalysisResult;
import com.example.unusedclassdetector.model.ClassReferenceGraph;
import com.example.unusedclassdetector.model.JavaSourceFile;
import com.example.unusedclassdetector.model.ProjectClassInfo;
import com.example.unusedclassdetector.parser.JavaSourceParser;
import com.example.unusedclassdetector.scanner.SourceFileScanner;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnusedClassDetectorTest {

    @Test
    void shouldScanJavaFilesRecursively() throws Exception {
        Path root = Files.createTempDirectory("unused-detector-scan");
        Path sourceRoot = root.resolve("src/main/java/com/example");
        Files.createDirectories(sourceRoot);
        Files.write(sourceRoot.resolve("One.java"), "class One {}".getBytes(StandardCharsets.UTF_8));
        Files.write(sourceRoot.resolve("Two.java"), "class Two {}".getBytes(StandardCharsets.UTF_8));

        List<String> warnings = new ArrayList<String>();
        List<Path> files = new SourceFileScanner().scan(
                new DetectorConfig(root.resolve("src/main/java"), Collections.<String>emptyList(), false),
                warnings
        );

        assertEquals(2, files.size());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void shouldParsePackageAndClassNameCorrectly() {
        String source = "package com.example.demo;\npublic class UserService {}";
        JavaSourceFile sourceFile = new JavaSourceParser().parse(Paths.get("UserService.java"), source);

        assertEquals("com.example.demo", sourceFile.getPackageName());
        assertEquals("UserService", sourceFile.getClassName());
        assertEquals("com.example.demo.UserService", sourceFile.getFullClassName());
    }

    @Test
    void shouldDetectReferencesAndIgnoreSelfReference() {
        JavaSourceFile app = new JavaSourceFile(
                Paths.get("AppMain.java"),
                "com.example.demo",
                "AppMain",
                "com.example.demo.AppMain",
                "package com.example.demo; public class AppMain { public static void main(String[] args){ UserService service = new UserService(); } }"
        );
        JavaSourceFile userService = new JavaSourceFile(
                Paths.get("UserService.java"),
                "com.example.demo",
                "UserService",
                "com.example.demo.UserService",
                "package com.example.demo; public class UserService { public UserService self(){ return this; } }"
        );

        DetectorConfig config = new DetectorConfig(Paths.get("."), Collections.<String>emptyList(), false);
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        List<ProjectClassInfo> classIndex = analyzer.buildClassIndex(Arrays.asList(app, userService), config);
        ClassReferenceGraph graph = analyzer.analyze(Arrays.asList(app, userService), classIndex);

        assertEquals(Integer.valueOf(1), graph.getInboundReferenceCounts().get("com.example.demo.UserService"));
        assertEquals(Integer.valueOf(0), graph.getInboundReferenceCounts().get("com.example.demo.AppMain"));
        assertFalse(graph.getOutboundReferences().get("com.example.demo.UserService").contains("com.example.demo.UserService"));
    }

    @Test
    void shouldDetectUnusedClassInSampleProject() {
        DetectorConfig config = new DetectorConfig(
                Paths.get("sample-project", "src", "main", "java"),
                Collections.singletonList(".*Test.*"),
                false
        );

        AnalysisResult result = new UnusedClassDetector().detect(config);

        assertTrue(result.getPotentiallyUnusedClasses().contains("com.example.sample.UnusedHelper"));
        assertTrue(result.getIgnoredClasses().contains("com.example.sample.AppMain"));
    }

    @Test
    void shouldRespectIgnorePatternAsRoot() {
        JavaSourceFile draftMapper = new JavaSourceFile(
                Paths.get("DraftMapper.java"),
                "com.example.demo",
                "DraftMapper",
                "com.example.demo.DraftMapper",
                "package com.example.demo; public class DraftMapper {}"
        );

        DetectorConfig config = new DetectorConfig(Paths.get("."), Collections.singletonList(".*Mapper"), false);
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        List<ProjectClassInfo> classIndex = analyzer.buildClassIndex(Collections.singletonList(draftMapper), config);

        assertTrue(classIndex.get(0).isRootClass());
    }
}
