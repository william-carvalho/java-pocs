package com.example.converterframework.core;

import com.example.converterframework.service.ConversionService;

public interface Converter<S, T> {

    T convert(S source, ConversionService conversionService);
}

