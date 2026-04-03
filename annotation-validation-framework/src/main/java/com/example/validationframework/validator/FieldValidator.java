package com.example.validationframework.validator;

import com.example.validationframework.model.ValidationError;

import java.lang.annotation.Annotation;

public interface FieldValidator<A extends Annotation> {

    ValidationError validate(A annotation, String fieldName, Object value);
}

