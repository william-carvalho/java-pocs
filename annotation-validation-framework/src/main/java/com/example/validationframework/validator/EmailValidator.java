package com.example.validationframework.validator;

import com.example.validationframework.annotation.Email;
import com.example.validationframework.model.ValidationError;

public class EmailValidator implements FieldValidator<Email> {

    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    public ValidationError validate(Email annotation, String fieldName, Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            return new ValidationError(fieldName, "Field type is not supported by @Email", value);
        }

        String text = (String) value;
        if (!EMAIL_PATTERN.matcher(text).matches()) {
            return new ValidationError(fieldName, annotation.message(), value);
        }
        return null;
    }
}

