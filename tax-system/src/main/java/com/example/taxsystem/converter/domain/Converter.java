package com.example.taxsystem.converter.domain;

import com.example.taxsystem.converter.application.ConverterEngine;

public interface Converter<S, T> {

    Class<S> sourceType();

    Class<T> targetType();

    T convert(S source, ConverterEngine converterEngine);
}
