package com.example.yamlcodegeneratorsingleclass;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YamlCodeGeneratorSingleClassApplicationTest {

    @Test
    void generatesJavaClassFromYamlDefinition() {
        YamlCodeGeneratorSingleClassApplication.CodeGenerator generator =
                new YamlCodeGeneratorSingleClassApplication.CodeGenerator();

        List<YamlCodeGeneratorSingleClassApplication.GeneratedFile> files = generator.generate(
                "codegen:\n" +
                        "  basePackage: com.example.generated\n" +
                        "  classes:\n" +
                        "    - name: User\n" +
                        "      generateGettersSetters: true\n" +
                        "      generateNoArgsConstructor: true\n" +
                        "      generateAllArgsConstructor: true\n" +
                        "      fields:\n" +
                        "        - name: id\n" +
                        "          type: Long\n" +
                        "        - name: name\n" +
                        "          type: String\n"
        );

        assertThat(files).hasSize(1);
        assertThat(files.get(0).path).isEqualTo("com/example/generated/User.java");
        assertThat(files.get(0).content).contains("package com.example.generated;");
        assertThat(files.get(0).content).contains("public class User");
        assertThat(files.get(0).content).contains("private Long id;");
        assertThat(files.get(0).content).contains("public User()");
        assertThat(files.get(0).content).contains("public User(Long id, String name)");
        assertThat(files.get(0).content).contains("public String getName()");
        assertThat(files.get(0).content).contains("public void setName(String name)");
    }

    @Test
    void generatesMultipleFiles() {
        YamlCodeGeneratorSingleClassApplication.CodeGenerator generator =
                new YamlCodeGeneratorSingleClassApplication.CodeGenerator();

        List<YamlCodeGeneratorSingleClassApplication.GeneratedFile> files = generator.generate(
                "codegen:\n" +
                        "  basePackage: com.example.generated\n" +
                        "  classes:\n" +
                        "    - name: User\n" +
                        "      fields:\n" +
                        "        - name: id\n" +
                        "          type: Long\n" +
                        "    - name: Address\n" +
                        "      fields:\n" +
                        "        - name: street\n" +
                        "          type: String\n"
        );

        assertThat(files).extracting("path")
                .containsExactly("com/example/generated/User.java", "com/example/generated/Address.java");
    }

    @Test
    void defaultsToNoArgsConstructorAndGettersSetters() {
        YamlCodeGeneratorSingleClassApplication.CodeGenerator generator =
                new YamlCodeGeneratorSingleClassApplication.CodeGenerator();

        String content = generator.generate(
                "codegen:\n" +
                        "  basePackage: com.example.generated\n" +
                        "  classes:\n" +
                        "    - name: Product\n" +
                        "      fields:\n" +
                        "        - name: sku\n" +
                        "          type: String\n"
        ).get(0).content;

        assertThat(content).contains("public Product()");
        assertThat(content).contains("public String getSku()");
        assertThat(content).doesNotContain("public Product(String sku)");
    }

    @Test
    void canDisableAccessorsAndConstructor() {
        YamlCodeGeneratorSingleClassApplication.CodeGenerator generator =
                new YamlCodeGeneratorSingleClassApplication.CodeGenerator();

        String content = generator.generate(
                "codegen:\n" +
                        "  basePackage: com.example.generated\n" +
                        "  classes:\n" +
                        "    - name: Product\n" +
                        "      generateGettersSetters: false\n" +
                        "      generateNoArgsConstructor: false\n" +
                        "      fields:\n" +
                        "        - name: sku\n" +
                        "          type: String\n"
        ).get(0).content;

        assertThat(content).doesNotContain("public Product()");
        assertThat(content).doesNotContain("getSku");
    }

    @Test
    void rejectsInvalidYamlDefinitions() {
        YamlCodeGeneratorSingleClassApplication.CodeGenerator generator =
                new YamlCodeGeneratorSingleClassApplication.CodeGenerator();

        assertThatThrownBy(() -> generator.generate("codegen:\n  classes: []\n"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("basePackage");

        assertThatThrownBy(() -> generator.generate("codegen:\n  basePackage: com.example\n  classes: []\n"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one class");

        assertThatThrownBy(() -> generator.generate("codegen:\n  basePackage: com.example\n  classes:\n    - name: User\n"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have fields");
    }
}
