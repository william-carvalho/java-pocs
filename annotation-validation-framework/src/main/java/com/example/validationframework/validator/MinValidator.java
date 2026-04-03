package com.example.validationframework.validator;

import com.example.validationframework.annotation.Min;
import com.example.validationframework.model.ValidationError;

public class MinValidator implements FieldValidator<Min> {

    @Override
    public ValidationError validate(Min annotation, String fieldName, Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Number)) {
            return new ValidationError(fieldName, "Field type is not supported by @Min", value);
        }

        Number number = (Number) value;
        if (number.doubleValue() < annotation.value()) {
            return new ValidationError(fieldName, annotation.message(), value);
        }
        return null;
    }
}

