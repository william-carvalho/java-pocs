package com.example.yamlcodegenerator.model;

import java.util.ArrayList;
import java.util.List;

public class ClassDefinition {

    private String name;
    private Boolean generateGettersSetters;
    private Boolean generateNoArgsConstructor;
    private Boolean generateAllArgsConstructor;
    private List<FieldDefinition> fields = new ArrayList<FieldDefinition>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGenerateGettersSetters() {
        return generateGettersSetters;
    }

    public void setGenerateGettersSetters(Boolean generateGettersSetters) {
        this.generateGettersSetters = generateGettersSetters;
    }

    public Boolean getGenerateNoArgsConstructor() {
        return generateNoArgsConstructor;
    }

    public void setGenerateNoArgsConstructor(Boolean generateNoArgsConstructor) {
        this.generateNoArgsConstructor = generateNoArgsConstructor;
    }

    public Boolean getGenerateAllArgsConstructor() {
        return generateAllArgsConstructor;
    }

    public void setGenerateAllArgsConstructor(Boolean generateAllArgsConstructor) {
        this.generateAllArgsConstructor = generateAllArgsConstructor;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields == null ? new ArrayList<FieldDefinition>() : fields;
    }

    public boolean shouldGenerateGettersSetters() {
        return Boolean.TRUE.equals(generateGettersSetters);
    }

    public boolean shouldGenerateNoArgsConstructor() {
        return Boolean.TRUE.equals(generateNoArgsConstructor);
    }

    public boolean shouldGenerateAllArgsConstructor() {
        return Boolean.TRUE.equals(generateAllArgsConstructor);
    }
}

