package com.example.yamlcodegenerator.parser;

import com.example.yamlcodegenerator.exception.InvalidYamlDefinitionException;
import com.example.yamlcodegenerator.model.ClassDefinition;
import com.example.yamlcodegenerator.model.CodegenConfig;
import com.example.yamlcodegenerator.model.CodegenDefinition;
import com.example.yamlcodegenerator.model.FieldDefinition;

import java.util.List;

public class DefinitionValidator {

    public void validate(CodegenDefinition definition) {
        if (definition == null || definition.getCodegen() == null) {
            throw new InvalidYamlDefinitionException("Codegen definition must not be null");
        }

        CodegenConfig config = definition.getCodegen();
        requireNotBlank(config.getBasePackage(), "basePackage is required");
        requireNotBlank(config.getOutputDir(), "outputDir is required");

        List<ClassDefinition> classes = config.getClasses();
        if (classes == null || classes.isEmpty()) {
            throw new InvalidYamlDefinitionException("At least one class definition is required");
        }

        for (ClassDefinition classDefinition : classes) {
            validateClass(classDefinition);
        }
    }

    private void validateClass(ClassDefinition classDefinition) {
        if (classDefinition == null) {
            throw new InvalidYamlDefinitionException("Class definition must not be null");
        }
        requireNotBlank(classDefinition.getName(), "Class name is required");

        List<FieldDefinition> fields = classDefinition.getFields();
        if (fields == null || fields.isEmpty()) {
            throw new InvalidYamlDefinitionException("Class '" + classDefinition.getName() + "' must have at least one field");
        }

        for (FieldDefinition fieldDefinition : fields) {
            validateField(classDefinition.getName(), fieldDefinition);
        }
    }

    private void validateField(String className, FieldDefinition fieldDefinition) {
        if (fieldDefinition == null) {
            throw new InvalidYamlDefinitionException("Field definition in class '" + className + "' must not be null");
        }
        requireNotBlank(fieldDefinition.getName(), "Field name is required in class '" + className + "'");
        requireNotBlank(fieldDefinition.getType(), "Field type is required in class '" + className + "'");
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidYamlDefinitionException(message);
        }
    }
}

