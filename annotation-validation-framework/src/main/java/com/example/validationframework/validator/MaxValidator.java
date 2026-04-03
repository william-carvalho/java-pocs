package com.example.validationframework.validator;

import com.example.validationframework.annotation.Max;
import com.example.validationframework.model.ValidationError;

public class MaxValidator implements FieldValidator<Max> {

    @Override
    public ValidationError validate(Max annotation, String fieldName, Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Number)) {
            return new ValidationError(fieldName, "Field type is not supported by @Max", value);
        }

        Number number = (Number) value;
        if (number.doubleValue() > annotation.value()) {
            return new ValidationError(fieldName, annotation.message(), value);
        }
        return null;
    }
}

