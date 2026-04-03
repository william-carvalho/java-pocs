package com.example.validationframework.engine;

import com.example.validationframework.annotation.Email;
import com.example.validationframework.annotation.Max;
import com.example.validationframework.annotation.Min;
import com.example.validationframework.annotation.NotBlank;
import com.example.validationframework.annotation.NotNull;
import com.example.validationframework.annotation.Pattern;
import com.example.validationframework.annotation.Size;
import com.example.validationframework.validator.EmailValidator;
import com.example.validationframework.validator.FieldValidator;
import com.example.validationframework.validator.MaxValidator;
import com.example.validationframework.validator.MinValidator;
import com.example.validationframework.validator.NotBlankValidator;
import com.example.validationframework.validator.NotNullValidator;
import com.example.validationframework.validator.PatternValidator;
import com.example.validationframework.validator.SizeValidator;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidatorRegistry {

    private final Map<Class<? extends Annotation>, FieldValidator<? extends Annotation>> validators =
            new ConcurrentHashMap<Class<? extends Annotation>, FieldValidator<? extends Annotation>>();

    public static ValidatorRegistry withDefaults() {
        ValidatorRegistry registry = new ValidatorRegistry();
        registry.registerDefaults();
        return registry;
    }

    public void registerDefaults() {
        register(NotNull.class, new NotNullValidator());
        register(NotBlank.class, new NotBlankValidator());
        register(Size.class, new SizeValidator());
        register(Min.class, new MinValidator());
        register(Max.class, new MaxValidator());
        register(Email.class, new EmailValidator());
        register(Pattern.class, new PatternValidator());
    }

    public <A extends Annotation> void register(Class<A> annotationType, FieldValidator<A> validator) {
        validators.put(annotationType, validator);
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> FieldValidator<A> getValidator(Class<A> annotationType) {
        return (FieldValidator<A>) validators.get(annotationType);
    }
}

