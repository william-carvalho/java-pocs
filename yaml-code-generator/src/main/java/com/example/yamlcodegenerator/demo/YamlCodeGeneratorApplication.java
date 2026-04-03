package com.example.yamlcodegenerator.demo;

import com.example.yamlcodegenerator.generator.CodeGenerator;
import com.example.yamlcodegenerator.generator.GeneratedFile;
import com.example.yamlcodegenerator.generator.JavaClassGenerator;
import com.example.yamlcodegenerator.model.CodegenDefinition;
import com.example.yamlcodegenerator.parser.DefinitionValidator;
import com.example.yamlcodegenerator.parser.YamlDefinitionParser;
import com.example.yamlcodegenerator.template.JavaTemplateRenderer;
import com.example.yamlcodegenerator.writer.FileOutputWriter;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class YamlCodeGeneratorApplication {

    public static void main(String[] args) {
        Path yamlPath = resolveYamlPath(args);

        YamlDefinitionParser parser = new YamlDefinitionParser();
        DefinitionValidator validator = new DefinitionValidator();
        CodeGenerator codeGenerator = new CodeGenerator(
                new JavaClassGenerator(new JavaTemplateRenderer(), new FileOutputWriter()));

        CodegenDefinition definition = parser.parse(yamlPath);
        validator.validate(definition);

        System.out.println("Loaded YAML definition successfully: " + yamlPath);
        List<GeneratedFile> generatedFiles = codeGenerator.generate(definition);
        for (GeneratedFile file : generatedFiles) {
            System.out.println("Generating class: " + file.getClassName());
            System.out.println("File written to: " + file.getOutputPath());
        }
        System.out.println("Files generated at: " + definition.getCodegen().getOutputDir());
    }

    private static Path resolveYamlPath(String[] args) {
        if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            return Paths.get(args[0]);
        }

        try {
            URI resourceUri = YamlCodeGeneratorApplication.class
                    .getClassLoader()
                    .getResource("sample-codegen.yml")
                    .toURI();
            return Paths.get(resourceUri);
        } catch (NullPointerException | URISyntaxException exception) {
            throw new IllegalStateException("Default sample-codegen.yml could not be resolved", exception);
        }
    }
}

