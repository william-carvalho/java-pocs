package com.example.validationframework.engine;

import com.example.validationframework.exception.ValidationFrameworkException;
import com.example.validationframework.model.ValidationError;
import com.example.validationframework.model.ValidationResult;
import com.example.validationframework.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class DefaultValidatorEngine implements ValidatorEngine {

    private final ValidatorRegistry registry;

    public DefaultValidatorEngine(ValidatorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public ValidationResult validate(Object target) {
        ValidationResult result = ValidationResult.valid();
        if (target == null) {
            result.addError(new ValidationError("$object", "Object to validate must not be null", null));
            return result;
        }

        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = readValue(field, target);

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                ValidationError error = validateFieldAnnotation(annotation, field.getName(), value);
                result.addError(error);
            }
        }

        return result;
    }

    private Object readValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException exception) {
            throw new ValidationFrameworkException(
                    "Could not read field '" + field.getName() + "' from " + target.getClass().getSimpleName(),
                    exception);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ValidationError validateFieldAnnotation(Annotation annotation, String fieldName, Object value) {
        FieldValidator validator = registry.getValidator(annotation.annotationType());
        if (validator == null) {
            return null;
        }
        return validator.validate(annotation, fieldName, value);
    }
}

