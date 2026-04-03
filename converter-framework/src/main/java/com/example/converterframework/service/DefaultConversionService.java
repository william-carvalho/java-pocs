package com.example.converterframework.service;

import com.example.converterframework.core.Converter;
import com.example.converterframework.registry.ConverterRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultConversionService implements ConversionService {

    private final ConverterRegistry converterRegistry;

    public DefaultConversionService(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }

        Converter<Object, T> converter = (Converter<Object, T>) converterRegistry.get(source.getClass(), targetType);
        return converter.convert(source, this);
    }

    @Override
    public <T> List<T> convertList(Collection<?> source, Class<T> targetType) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> converted = new ArrayList<T>(source.size());
        for (Object item : source) {
            converted.add(convert(item, targetType));
        }
        return converted;
    }
}

