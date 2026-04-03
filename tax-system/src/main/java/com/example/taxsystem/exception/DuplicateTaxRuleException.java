package com.example.taxsystem.exception;

public class DuplicateTaxRuleException extends RuntimeException {

    public DuplicateTaxRuleException(String message) {
        super(message);
    }
}
