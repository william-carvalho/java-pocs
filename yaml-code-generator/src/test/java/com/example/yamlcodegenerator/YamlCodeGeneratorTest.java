package com.example.yamlcodegenerator;

import com.example.yamlcodegenerator.exception.InvalidYamlDefinitionException;
import com.example.yamlcodegenerator.generator.JavaClassGenerator;
import com.example.yamlcodegenerator.model.ClassDefinition;
import com.example.yamlcodegenerator.model.CodegenConfig;
import com.example.yamlcodegenerator.model.CodegenDefinition;
import com.example.yamlcodegenerator.model.FieldDefinition;
import com.example.yamlcodegenerator.parser.DefinitionValidator;
import com.example.yamlcodegenerator.parser.YamlDefinitionParser;
import com.example.yamlcodegenerator.template.JavaTemplateRenderer;
import com.example.yamlcodegenerator.writer.FileOutputWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlCodeGeneratorTest {

    private YamlDefinitionParser parser;
    private DefinitionValidator validator;
    private JavaTemplateRenderer renderer;

    @BeforeEach
    void setUp() {
        parser = new YamlDefinitionParser();
        validator = new DefinitionValidator();
        renderer = new JavaTemplateRenderer();
    }

    @Test
    void shouldParseYamlDefinitionCorrectly() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("valid-codegen.yml");

        CodegenDefinition definition = parser.parse(inputStream);

        assertEquals("com.example.generated", definition.getCodegen().getBasePackage());
        assertEquals(1, definition.getCodegen().getClasses().size());
        assertEquals("User", definition.getCodegen().getClasses().get(0).getName());
    }

    @Test
    void shouldFailValidationWhenClassNameIsMissing() {
        CodegenDefinition definition = new CodegenDefinition();
        CodegenConfig config = new CodegenConfig();
        config.setBasePackage("com.example.generated");
        config.setOutputDir("./generated-src");
        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setFields(Collections.singletonList(field("id", "Long")));
        config.setClasses(Collections.singletonList(classDefinition));
        definition.setCodegen(config);

        assertThrows(InvalidYamlDefinitionException.class, () -> validator.validate(definition));
    }

    @Test
    void shouldFailValidationWhenFieldNameOrTypeIsMissing() {
        CodegenDefinition definition = new CodegenDefinition();
        CodegenConfig config = new CodegenConfig();
        config.setBasePackage("com.example.generated");
        config.setOutputDir("./generated-src");
        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("User");
        classDefinition.setFields(Arrays.asList(field("", "Long"), field("name", "")));
        config.setClasses(Collections.singletonList(classDefinition));
        definition.setCodegen(config);

        assertThrows(InvalidYamlDefinitionException.class, () -> validator.validate(definition));
    }

    @Test
    void shouldGenerateJavaClassContentCorrectly() {
        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("User");
        classDefinition.setGenerateGettersSetters(true);
        classDefinition.setGenerateNoArgsConstructor(true);
        classDefinition.setGenerateAllArgsConstructor(true);
        classDefinition.setFields(Arrays.asList(field("id", "Long"), field("name", "String")));

        String content = renderer.render("com.example.generated", classDefinition);

        assertTrue(content.contains("package com.example.generated;"));
        assertTrue(content.contains("public class User {"));
        assertTrue(content.contains("private Long id;"));
        assertTrue(content.contains("public User() {"));
        assertTrue(content.contains("public User(Long id, String name) {"));
        assertTrue(content.contains("public Long getId() {"));
        assertTrue(content.contains("public void setName(String name) {"));
    }

    @Test
    void shouldWriteGeneratedFile(@TempDir Path tempDir) throws Exception {
        CodegenConfig config = new CodegenConfig();
        config.setBasePackage("com.example.generated");
        config.setOutputDir(tempDir.toString());

        JavaClassGenerator generator = new JavaClassGenerator(renderer, new FileOutputWriter());
        Path outputFile = generator.resolveOutputFile(config, "User");

        new FileOutputWriter().write(outputFile, "public class User {}");

        assertTrue(Files.exists(outputFile));
        assertEquals("public class User {}", new String(Files.readAllBytes(outputFile), StandardCharsets.UTF_8));
    }

    @Test
    void shouldResolveOutputPathCorrectly(@TempDir Path tempDir) {
        CodegenConfig config = new CodegenConfig();
        config.setBasePackage("com.example.generated");
        config.setOutputDir(tempDir.toString());

        JavaClassGenerator generator = new JavaClassGenerator(renderer, new FileOutputWriter());
        Path outputFile = generator.resolveOutputFile(config, "User");

        assertTrue(outputFile.toString().endsWith("com\\example\\generated\\User.java")
                || outputFile.toString().endsWith("com/example/generated/User.java"));
    }

    private static FieldDefinition field(String name, String type) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setName(name);
        fieldDefinition.setType(type);
        return fieldDefinition;
    }
}

