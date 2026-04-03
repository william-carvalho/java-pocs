package com.example.yamlcodegenerator.generator;

import com.example.yamlcodegenerator.model.ClassDefinition;
import com.example.yamlcodegenerator.model.CodegenConfig;
import com.example.yamlcodegenerator.template.JavaTemplateRenderer;
import com.example.yamlcodegenerator.writer.FileOutputWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaClassGenerator {

    private final JavaTemplateRenderer renderer;
    private final FileOutputWriter fileOutputWriter;

    public JavaClassGenerator(JavaTemplateRenderer renderer, FileOutputWriter fileOutputWriter) {
        this.renderer = renderer;
        this.fileOutputWriter = fileOutputWriter;
    }

    public GeneratedFile generate(CodegenConfig config, ClassDefinition classDefinition) {
        String content = renderer.render(config.getBasePackage(), classDefinition);
        Path outputFile = resolveOutputFile(config, classDefinition.getName());
        fileOutputWriter.write(outputFile, content);
        return new GeneratedFile(classDefinition.getName(), outputFile);
    }

    public Path resolveOutputFile(CodegenConfig config, String className) {
        String packagePath = config.getBasePackage().replace('.', java.io.File.separatorChar);
        return Paths.get(config.getOutputDir(), packagePath, className + ".java");
    }
}

