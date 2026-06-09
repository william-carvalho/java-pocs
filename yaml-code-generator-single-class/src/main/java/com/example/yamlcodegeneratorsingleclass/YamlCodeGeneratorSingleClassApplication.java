package com.example.yamlcodegeneratorsingleclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class YamlCodeGeneratorSingleClassApplication {

    private final CodeGenerator generator = new CodeGenerator();

    public static void main(String[] args) {
        SpringApplication.run(YamlCodeGeneratorSingleClassApplication.class, args);
    }

    @PostMapping("/generate")
    public List<GeneratedFile> generate(@RequestBody String yaml) {
        try {
            return generator.generate(yaml);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    public CodeGenerator getGenerator() {
        return generator;
    }

    public static class CodeGenerator {
        public List<GeneratedFile> generate(String yamlText) {
            Definition definition = parse(yamlText);
            validate(definition);

            List<GeneratedFile> files = new ArrayList<GeneratedFile>();
            for (ClassDefinition classDefinition : definition.classes) {
                files.add(new GeneratedFile(
                        definition.basePackage.replace('.', '/') + "/" + classDefinition.name + ".java",
                        render(definition.basePackage, classDefinition)
                ));
            }
            return files;
        }

        private Definition parse(String yamlText) {
            if (yamlText == null || yamlText.trim().isEmpty()) {
                throw new IllegalArgumentException("yaml definition is required");
            }

            Object loaded = new Yaml().load(yamlText);
            if (!(loaded instanceof Map)) {
                throw new IllegalArgumentException("yaml root must be an object");
            }

            Map<?, ?> root = (Map<?, ?>) loaded;
            Object codegenObject = root.get("codegen");
            if (!(codegenObject instanceof Map)) {
                throw new IllegalArgumentException("codegen section is required");
            }

            Map<?, ?> codegen = (Map<?, ?>) codegenObject;
            Definition definition = new Definition();
            definition.basePackage = string(codegen.get("basePackage"));

            Object classesObject = codegen.get("classes");
            if (classesObject instanceof List) {
                for (Object classObject : (List<?>) classesObject) {
                    definition.classes.add(parseClass(classObject));
                }
            }

            return definition;
        }

        private ClassDefinition parseClass(Object classObject) {
            if (!(classObject instanceof Map)) {
                throw new IllegalArgumentException("class definition must be an object");
            }

            Map<?, ?> classMap = (Map<?, ?>) classObject;
            ClassDefinition classDefinition = new ClassDefinition();
            classDefinition.name = string(classMap.get("name"));
            classDefinition.generateGettersSetters = bool(classMap.get("generateGettersSetters"), true);
            classDefinition.generateNoArgsConstructor = bool(classMap.get("generateNoArgsConstructor"), true);
            classDefinition.generateAllArgsConstructor = bool(classMap.get("generateAllArgsConstructor"), false);

            Object fieldsObject = classMap.get("fields");
            if (fieldsObject instanceof List) {
                for (Object fieldObject : (List<?>) fieldsObject) {
                    classDefinition.fields.add(parseField(fieldObject));
                }
            }

            return classDefinition;
        }

        private FieldDefinition parseField(Object fieldObject) {
            if (!(fieldObject instanceof Map)) {
                throw new IllegalArgumentException("field definition must be an object");
            }

            Map<?, ?> fieldMap = (Map<?, ?>) fieldObject;
            FieldDefinition fieldDefinition = new FieldDefinition();
            fieldDefinition.name = string(fieldMap.get("name"));
            fieldDefinition.type = string(fieldMap.get("type"));
            return fieldDefinition;
        }

        private void validate(Definition definition) {
            if (blank(definition.basePackage)) {
                throw new IllegalArgumentException("basePackage is required");
            }
            if (definition.classes.isEmpty()) {
                throw new IllegalArgumentException("at least one class is required");
            }
            for (ClassDefinition classDefinition : definition.classes) {
                if (blank(classDefinition.name)) {
                    throw new IllegalArgumentException("class name is required");
                }
                if (classDefinition.fields.isEmpty()) {
                    throw new IllegalArgumentException("class " + classDefinition.name + " must have fields");
                }
                for (FieldDefinition fieldDefinition : classDefinition.fields) {
                    if (blank(fieldDefinition.name) || blank(fieldDefinition.type)) {
                        throw new IllegalArgumentException("field name and type are required");
                    }
                }
            }
        }

        private String render(String basePackage, ClassDefinition classDefinition) {
            StringBuilder code = new StringBuilder();
            code.append("package ").append(basePackage).append(";\n\n");
            code.append("public class ").append(classDefinition.name).append(" {\n\n");

            for (FieldDefinition field : classDefinition.fields) {
                code.append("    private ").append(field.type).append(" ").append(field.name).append(";\n");
            }

            if (classDefinition.generateNoArgsConstructor) {
                code.append("\n    public ").append(classDefinition.name).append("() {\n");
                code.append("    }\n");
            }

            if (classDefinition.generateAllArgsConstructor) {
                code.append("\n    public ").append(classDefinition.name).append("(");
                for (int i = 0; i < classDefinition.fields.size(); i++) {
                    FieldDefinition field = classDefinition.fields.get(i);
                    if (i > 0) {
                        code.append(", ");
                    }
                    code.append(field.type).append(" ").append(field.name);
                }
                code.append(") {\n");
                for (FieldDefinition field : classDefinition.fields) {
                    code.append("        this.").append(field.name).append(" = ").append(field.name).append(";\n");
                }
                code.append("    }\n");
            }

            if (classDefinition.generateGettersSetters) {
                for (FieldDefinition field : classDefinition.fields) {
                    String methodSuffix = upperFirst(field.name);
                    code.append("\n    public ").append(field.type).append(" get").append(methodSuffix).append("() {\n");
                    code.append("        return ").append(field.name).append(";\n");
                    code.append("    }\n");
                    code.append("\n    public void set").append(methodSuffix).append("(")
                            .append(field.type).append(" ").append(field.name).append(") {\n");
                    code.append("        this.").append(field.name).append(" = ").append(field.name).append(";\n");
                    code.append("    }\n");
                }
            }

            code.append("}\n");
            return code.toString();
        }

        private boolean blank(String value) {
            return value == null || value.trim().isEmpty();
        }

        private String string(Object value) {
            return value == null ? null : String.valueOf(value);
        }

        private boolean bool(Object value, boolean defaultValue) {
            return value == null ? defaultValue : Boolean.parseBoolean(String.valueOf(value));
        }

        private String upperFirst(String value) {
            return value.substring(0, 1).toUpperCase() + value.substring(1);
        }
    }

    public static class Definition {
        public String basePackage;
        public List<ClassDefinition> classes = new ArrayList<ClassDefinition>();
    }

    public static class ClassDefinition {
        public String name;
        public boolean generateGettersSetters;
        public boolean generateNoArgsConstructor;
        public boolean generateAllArgsConstructor;
        public List<FieldDefinition> fields = new ArrayList<FieldDefinition>();
    }

    public static class FieldDefinition {
        public String name;
        public String type;
    }

    public static class GeneratedFile {
        public String path;
        public String content;

        public GeneratedFile() {
        }

        public GeneratedFile(String path, String content) {
            this.path = path;
            this.content = content;
        }
    }
}
