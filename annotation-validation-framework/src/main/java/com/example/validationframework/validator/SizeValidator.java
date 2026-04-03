package com.example.validationframework.validator;

import com.example.validationframework.annotation.Size;
import com.example.validationframework.model.ValidationError;

public class SizeValidator implements FieldValidator<Size> {

    @Override
    public ValidationError validate(Size annotation, String fieldName, Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            return new ValidationError(fieldName, "Field type is not supported by @Size", value);
        }

        int length = ((String) value).length();
        if (length < annotation.min() || length > annotation.max()) {
            return new ValidationError(fieldName, annotation.message(), value);
        }
        return null;
    }
}

