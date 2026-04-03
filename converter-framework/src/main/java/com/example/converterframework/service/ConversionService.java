package com.example.converterframework.service;

import java.util.Collection;
import java.util.List;

public interface ConversionService {

    <T> T convert(Object source, Class<T> targetType);

    <T> List<T> convertList(Collection<?> source, Class<T> targetType);
}

