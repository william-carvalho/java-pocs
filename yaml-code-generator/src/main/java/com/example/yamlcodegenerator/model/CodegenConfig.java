package com.example.yamlcodegenerator.model;

import java.util.ArrayList;
import java.util.List;

public class CodegenConfig {

    private String basePackage;
    private String outputDir;
    private List<ClassDefinition> classes = new ArrayList<ClassDefinition>();

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public List<ClassDefinition> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassDefinition> classes) {
        this.classes = classes == null ? new ArrayList<ClassDefinition>() : classes;
    }
}

