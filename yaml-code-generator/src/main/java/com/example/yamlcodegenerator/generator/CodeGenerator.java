package com.example.yamlcodegenerator.generator;

import com.example.yamlcodegenerator.model.ClassDefinition;
import com.example.yamlcodegenerator.model.CodegenConfig;
import com.example.yamlcodegenerator.model.CodegenDefinition;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private final JavaClassGenerator javaClassGenerator;

    public CodeGenerator(JavaClassGenerator javaClassGenerator) {
        this.javaClassGenerator = javaClassGenerator;
    }

    public List<GeneratedFile> generate(CodegenDefinition definition) {
        CodegenConfig config = definition.getCodegen();
        List<GeneratedFile> files = new ArrayList<GeneratedFile>();
        for (ClassDefinition classDefinition : config.getClasses()) {
            files.add(javaClassGenerator.generate(config, classDefinition));
        }
        return files;
    }
}

