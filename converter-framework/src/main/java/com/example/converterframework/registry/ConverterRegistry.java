package com.example.converterframework.registry;

import com.example.converterframework.core.Converter;
import com.example.converterframework.exception.ConverterNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterRegistry {

    private final Map<ConverterKey, Converter<?, ?>> converters = new ConcurrentHashMap<ConverterKey, Converter<?, ?>>();

    public <S, T> void register(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter) {
        converters.put(new ConverterKey(sourceType, targetType), converter);
    }

    @SuppressWarnings("unchecked")
    public <S, T> Converter<S, T> get(Class<S> sourceType, Class<T> targetType) {
        Converter<?, ?> converter = converters.get(new ConverterKey(sourceType, targetType));
        if (converter == null) {
            throw new ConverterNotFoundException(
                    "No converter registered for source " + sourceType.getSimpleName() +
                            " and target " + targetType.getSimpleName()
            );
        }
        return (Converter<S, T>) converter;
    }
}

