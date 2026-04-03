package com.example.validationframework.validator;

import com.example.validationframework.annotation.NotBlank;
import com.example.validationframework.model.ValidationError;

public class NotBlankValidator implements FieldValidator<NotBlank> {

    @Override
    public ValidationError validate(NotBlank annotation, String fieldName, Object value) {
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            return new ValidationError(fieldName, annotation.message(), value);
        }
        return null;
    }
}

