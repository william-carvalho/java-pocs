package com.example.yamlcodegenerator.template;

import com.example.yamlcodegenerator.model.ClassDefinition;
import com.example.yamlcodegenerator.model.FieldDefinition;
import com.example.yamlcodegenerator.util.NameUtils;

import java.util.List;

public class JavaTemplateRenderer {

    public String render(String basePackage, ClassDefinition classDefinition) {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(basePackage).append(";").append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("public class ").append(classDefinition.getName()).append(" {").append(System.lineSeparator()).append(System.lineSeparator());

        appendFields(builder, classDefinition.getFields());

        if (classDefinition.shouldGenerateNoArgsConstructor()) {
            appendNoArgsConstructor(builder, classDefinition.getName());
        }

        if (classDefinition.shouldGenerateAllArgsConstructor()) {
            appendAllArgsConstructor(builder, classDefinition.getName(), classDefinition.getFields());
        }

        if (classDefinition.shouldGenerateGettersSetters()) {
            appendGettersAndSetters(builder, classDefinition.getFields());
        }

        builder.append("}").append(System.lineSeparator());
        return builder.toString();
    }

    private void appendFields(StringBuilder builder, List<FieldDefinition> fields) {
        for (FieldDefinition field : fields) {
            builder.append("    private ").append(field.getType()).append(" ").append(field.getName());
            if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                builder.append(" = ").append(field.getDefaultValue());
            }
            builder.append(";").append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
    }

    private void appendNoArgsConstructor(StringBuilder builder, String className) {
        builder.append("    public ").append(className).append("() {").append(System.lineSeparator());
        builder.append("    }").append(System.lineSeparator()).append(System.lineSeparator());
    }

    private void appendAllArgsConstructor(StringBuilder builder, String className, List<FieldDefinition> fields) {
        builder.append("    public ").append(className).append("(");
        for (int index = 0; index < fields.size(); index++) {
            FieldDefinition field = fields.get(index);
            if (index > 0) {
                builder.append(", ");
            }
            builder.append(field.getType()).append(" ").append(field.getName());
        }
        builder.append(") {").append(System.lineSeparator());
        for (FieldDefinition field : fields) {
            builder.append("        this.").append(field.getName()).append(" = ").append(field.getName()).append(";")
                    .append(System.lineSeparator());
        }
        builder.append("    }").append(System.lineSeparator()).append(System.lineSeparator());
    }

    private void appendGettersAndSetters(StringBuilder builder, List<FieldDefinition> fields) {
        for (FieldDefinition field : fields) {
            String capitalizedName = NameUtils.capitalize(field.getName());
            builder.append("    public ").append(field.getType()).append(" get").append(capitalizedName).append("() {")
                    .append(System.lineSeparator());
            builder.append("        return ").append(field.getName()).append(";").append(System.lineSeparator());
            builder.append("    }").append(System.lineSeparator()).append(System.lineSeparator());

            builder.append("    public void set").append(capitalizedName).append("(")
                    .append(field.getType()).append(" ").append(field.getName()).append(") {").append(System.lineSeparator());
            builder.append("        this.").append(field.getName()).append(" = ").append(field.getName()).append(";")
                    .append(System.lineSeparator());
            builder.append("    }").append(System.lineSeparator()).append(System.lineSeparator());
        }
    }
}

