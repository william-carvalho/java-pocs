package com.example.unusedclass;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnusedClassDetectorTest {
    @TempDir
    Path tempDir;

    @Test
    void scansJavaFilesRecursively() throws Exception {
        Path root = tempDir.resolve("src/main/java");
        write(root.resolve("com/example/AppMain.java"), "package com.example; class AppMain {}");
        write(root.resolve("com/example/internal/Helper.java"), "package com.example.internal; class Helper {}");
        write(root.resolve("README.md"), "ignored");

        List<UnusedClassDetector.JavaSourceFile> files = new UnusedClassDetector().scan(root, false);

        assertEquals(2, files.size());
    }

    @Test
    void parsesPackageAndTopLevelType() throws Exception {
        Path file = tempDir.resolve("UserService.java");
        write(file, "package com.example.service; public class UserService {}");

        UnusedClassDetector.ProjectClass parsed = new UnusedClassDetector().parse(source(file));

        assertEquals("com.example.service", parsed.getPackageName());
        assertEquals("UserService", parsed.getSimpleName());
        assertEquals("com.example.service.UserService", parsed.getQualifiedName());
    }

    @Test
    void detectsPotentiallyUnusedClass() throws Exception {
        Path root = sampleProject();

        UnusedClassDetector.AnalysisResult result = new UnusedClassDetector().detect(UnusedClassDetector.config(root));

        assertContains(result.getPotentiallyUnusedClasses(), "com.example.UnusedHelper");
        assertFalse(contains(result.getPotentiallyUnusedClasses(), "com.example.UserService"));
        assertFalse(contains(result.getPotentiallyUnusedClasses(), "com.example.AppMain"));
    }

    @Test
    void buildsReferenceGraphAndIgnoresSelfReference() throws Exception {
        Path root = sampleProject();

        UnusedClassDetector.AnalysisResult result = new UnusedClassDetector().detect(UnusedClassDetector.config(root));
        Set<String> appReferences = result.getReferences().get("com.example.AppMain");

        assertTrue(appReferences.contains("com.example.UserController"));
        assertFalse(result.getReferences().get("com.example.UserService").contains("com.example.UserService"));
    }

    @Test
    void ignoresMainApplicationAndConfigRoots() throws Exception {
        Path root = tempDir.resolve("roots");
        write(root.resolve("com/example/DemoApplication.java"), "package com.example; public class DemoApplication {}");
        write(root.resolve("com/example/AppMain.java"), "package com.example; public class AppMain {}");
        write(root.resolve("com/example/AppConfig.java"), "package com.example; public class AppConfig {}");
        write(root.resolve("com/example/config/WebSettings.java"), "package com.example.config; public class WebSettings {}");

        UnusedClassDetector.AnalysisResult result = new UnusedClassDetector().detect(UnusedClassDetector.config(root));

        assertEquals(4, result.getIgnoredClasses().size());
        assertEquals(0, result.getPotentiallyUnusedClasses().size());
    }

    @Test
    void supportsIgnorePatterns() throws Exception {
        Path root = sampleProject();
        UnusedClassDetector.DetectorConfig config = new UnusedClassDetector.DetectorConfig(
                root,
                Arrays.asList(".*UnusedHelper"),
                false);

        UnusedClassDetector.AnalysisResult result = new UnusedClassDetector().detect(config);

        assertFalse(contains(result.getPotentiallyUnusedClasses(), "com.example.UnusedHelper"));
        assertContains(result.getIgnoredClasses(), "com.example.UnusedHelper");
    }

    @Test
    void stripsCommentsAndStringsBeforeReferenceDetection() throws Exception {
        Path root = tempDir.resolve("comments");
        write(root.resolve("com/example/AppMain.java"),
                "package com.example; public class AppMain { public static void main(String[] args) { new UsedClass(); } }");
        write(root.resolve("com/example/UsedClass.java"),
                "package com.example; public class UsedClass { String s = \"UnusedByString\"; /* CommentedReference */ }");
        write(root.resolve("com/example/UnusedByString.java"),
                "package com.example; public class UnusedByString {}");
        write(root.resolve("com/example/CommentedReference.java"),
                "package com.example; public class CommentedReference {}");

        UnusedClassDetector.AnalysisResult result = new UnusedClassDetector().detect(UnusedClassDetector.config(root));

        assertContains(result.getPotentiallyUnusedClasses(), "com.example.UnusedByString");
        assertContains(result.getPotentiallyUnusedClasses(), "com.example.CommentedReference");
    }

    @Test
    void excludesTestSourcesUnlessIncluded() throws Exception {
        Path root = tempDir.resolve("project");
        write(root.resolve("src/main/java/com/example/AppMain.java"), "package com.example; class AppMain {}");
        write(root.resolve("src/test/java/com/example/AppMainTest.java"), "package com.example; class AppMainTest {}");

        UnusedClassDetector detector = new UnusedClassDetector();

        assertEquals(1, detector.scan(root, false).size());
        assertEquals(2, detector.scan(root, true).size());
    }

    @Test
    void reportContainsSummaryAndUnusedNames() throws Exception {
        Path root = sampleProject();

        String report = new UnusedClassDetector().detect(UnusedClassDetector.config(root)).toTextReport();

        assertTrue(report.contains("Unused Class Detector Report"));
        assertTrue(report.contains("Total classes found: 5"));
        assertTrue(report.contains("- com.example.UnusedHelper"));
    }

    @Test
    void rejectsMissingSourceRoot() {
        assertThrows(IllegalArgumentException.class, () ->
                new UnusedClassDetector().detect(UnusedClassDetector.config(tempDir.resolve("missing"))));
    }

    private Path sampleProject() throws Exception {
        Path root = tempDir.resolve("sample/src/main/java");
        write(root.resolve("com/example/AppMain.java"),
                "package com.example; public class AppMain { public static void main(String[] args) { new UserController(); } }");
        write(root.resolve("com/example/UserController.java"),
                "package com.example; public class UserController { private final UserService service = new UserService(); }");
        write(root.resolve("com/example/UserService.java"),
                "package com.example; public class UserService { private UserRepository repository = new UserRepository(); }");
        write(root.resolve("com/example/UserRepository.java"),
                "package com.example; public class UserRepository {}");
        write(root.resolve("com/example/UnusedHelper.java"),
                "package com.example; public class UnusedHelper {}");
        return root;
    }

    private static UnusedClassDetector.JavaSourceFile source(Path path) throws Exception {
        return new UnusedClassDetector.JavaSourceFile(path, new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
    }

    private static void write(Path path, String content) throws Exception {
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void assertContains(List<UnusedClassDetector.ProjectClass> classes, String qualifiedName) {
        assertTrue(contains(classes, qualifiedName), "Expected " + qualifiedName);
    }

    private static boolean contains(List<UnusedClassDetector.ProjectClass> classes, String qualifiedName) {
        for (UnusedClassDetector.ProjectClass projectClass : classes) {
            if (projectClass.getQualifiedName().equals(qualifiedName)) {
                return true;
            }
        }
        return false;
    }
}
