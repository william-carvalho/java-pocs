package com.example.validationframework.util;

import com.example.validationframework.model.ValidationError;
import com.example.validationframework.model.ValidationResult;

public final class ValidationPrinter {

    private ValidationPrinter() {
    }

    public static void print(String label, ValidationResult result) {
        System.out.println("=== " + label + " ===");
        System.out.println("Valid: " + result.isValid());
        if (result.isValid()) {
            System.out.println("No validation errors.");
            return;
        }

        for (ValidationError error : result.getErrors()) {
            System.out.println("- field=" + error.getField()
                    + ", message=" + error.getMessage()
                    + ", rejectedValue=" + error.getRejectedValue());
        }
    }
}

