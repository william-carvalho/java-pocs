package com.example.validationframework.demo;

import com.example.validationframework.engine.DefaultValidatorEngine;
import com.example.validationframework.engine.ValidatorEngine;
import com.example.validationframework.engine.ValidatorRegistry;
import com.example.validationframework.model.ValidationResult;
import com.example.validationframework.util.ValidationPrinter;

public class ValidationFrameworkDemo {

    public static void main(String[] args) {
        ValidatorEngine validatorEngine = new DefaultValidatorEngine(ValidatorRegistry.withDefaults());

        UserRequest invalidUser = new UserRequest("", "invalid-email", "123", 15);
        UserRequest validUser = new UserRequest("William Carvalho", "william@email.com", "strongPass1", 30);
        ProductRequest invalidProduct = new ProductRequest("", "short", 0.0, "sku-1");
        AddressRequest validAddress = new AddressRequest("Street 1", "Floripa", "88000-000");

        ValidationResult invalidUserResult = validatorEngine.validate(invalidUser);
        ValidationResult validUserResult = validatorEngine.validate(validUser);
        ValidationResult invalidProductResult = validatorEngine.validate(invalidProduct);
        ValidationResult validAddressResult = validatorEngine.validate(validAddress);

        ValidationPrinter.print("Invalid User", invalidUserResult);
        ValidationPrinter.print("Valid User", validUserResult);
        ValidationPrinter.print("Invalid Product", invalidProductResult);
        ValidationPrinter.print("Valid Address", validAddressResult);
    }
}
