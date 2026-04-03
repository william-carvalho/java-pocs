package com.example.yamlcodegenerator.exception;

public class InvalidYamlDefinitionException extends CodegenException {

    public InvalidYamlDefinitionException(String message) {
        super(message);
    }

    public InvalidYamlDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}

