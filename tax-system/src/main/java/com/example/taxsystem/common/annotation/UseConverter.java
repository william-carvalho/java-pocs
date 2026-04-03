package com.example.taxsystem.common.annotation;

import com.example.taxsystem.converter.domain.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UseConverter {
    Class<? extends Converter<?, ?>> value();
}
