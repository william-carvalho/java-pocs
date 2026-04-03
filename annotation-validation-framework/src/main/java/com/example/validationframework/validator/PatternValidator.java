package com.example.validationframework.validator;

import com.example.validationframework.model.ValidationError;

public class PatternValidator implements FieldValidator<com.example.validationframework.annotation.Pattern> {

    @Override
    public ValidationError validate(com.example.validationframework.annotation.Pattern annotation,
                                    String fieldName,
                                    Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            return new ValidationError(fieldName, "Field type is not supported by @Pattern", value);
        }

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(annotation.regex());
        if (!pattern.matcher((String) value).matches()) {
            return new ValidationError(fieldName, annotation.message(), value);
        }
        return null;
    }
}
