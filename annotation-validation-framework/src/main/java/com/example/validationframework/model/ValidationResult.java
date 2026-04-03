package com.example.validationframework.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final List<ValidationError> errors = new ArrayList<ValidationError>();

    public static ValidationResult valid() {
        return new ValidationResult();
    }

    public void addError(ValidationError error) {
        if (error != null) {
            errors.add(error);
        }
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}

