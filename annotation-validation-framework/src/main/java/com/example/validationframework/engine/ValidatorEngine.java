package com.example.validationframework.engine;

import com.example.validationframework.model.ValidationResult;

public interface ValidatorEngine {

    ValidationResult validate(Object target);
}

