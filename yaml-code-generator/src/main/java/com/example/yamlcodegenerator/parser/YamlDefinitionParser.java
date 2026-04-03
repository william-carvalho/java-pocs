package com.example.yamlcodegenerator.parser;

import com.example.yamlcodegenerator.exception.InvalidYamlDefinitionException;
import com.example.yamlcodegenerator.model.CodegenDefinition;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlDefinitionParser {

    public CodegenDefinition parse(Path yamlPath) {
        try (InputStream inputStream = Files.newInputStream(yamlPath)) {
            return parse(inputStream);
        } catch (IOException exception) {
            throw new InvalidYamlDefinitionException("Could not read YAML definition: " + yamlPath, exception);
        }
    }

    public CodegenDefinition parse(InputStream inputStream) {
        try {
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(CodegenDefinition.class, loaderOptions);
            Yaml yaml = new Yaml(constructor);
            CodegenDefinition definition = yaml.load(inputStream);
            if (definition == null || definition.getCodegen() == null) {
                throw new InvalidYamlDefinitionException("YAML must contain a root 'codegen' definition");
            }
            return definition;
        } catch (RuntimeException exception) {
            if (exception instanceof InvalidYamlDefinitionException) {
                throw exception;
            }
            throw new InvalidYamlDefinitionException("Invalid YAML format", exception);
        }
    }
}

