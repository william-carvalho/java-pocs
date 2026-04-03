package com.example.validationframework.validator;

import com.example.validationframework.annotation.NotNull;
import com.example.validationframework.model.ValidationError;

public class NotNullValidator implements FieldValidator<NotNull> {

    @Override
    public ValidationError validate(NotNull annotation, String fieldName, Object value) {
        if (value == null) {
            return new ValidationError(fieldName, annotation.message(), null);
        }
        return null;
    }
}

