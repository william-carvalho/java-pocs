package com.example.taxsystem.converter.application;

import com.example.taxsystem.common.exception.ConversionException;
import com.example.taxsystem.converter.domain.Converter;
import com.example.taxsystem.converter.domain.TypePair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConverterRegistry {

    private final Map<TypePair, Converter<?, ?>> converters = new ConcurrentHashMap<TypePair, Converter<?, ?>>();
    private final Map<Class<? extends Converter<?, ?>>, Converter<?, ?>> convertersByClass =
            new ConcurrentHashMap<Class<? extends Converter<?, ?>>, Converter<?, ?>>();

    public ConverterRegistry(List<Converter<?, ?>> discoveredConverters) {
        discoveredConverters.forEach(this::register);
    }

    public void register(Converter<?, ?> converter) {
        converters.put(new TypePair(converter.sourceType(), converter.targetType()), converter);
        convertersByClass.put(castConverterClass(converter.getClass()), converter);
    }

    @SuppressWarnings("unchecked")
    public Optional<Converter<Object, Object>> find(Class<?> sourceType, Class<?> targetType) {
        return converters.values()
                .stream()
                .filter(converter -> converter.sourceType().isAssignableFrom(sourceType))
                .filter(converter -> targetType.isAssignableFrom(converter.targetType()))
                .map(converter -> (Converter<Object, Object>) converter)
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public Converter<Object, Object> getRequiredByClass(Class<? extends Converter<?, ?>> converterClass) {
        Converter<?, ?> converter = convertersByClass.get(converterClass);
        if (converter == null) {
            throw new ConversionException("Converter not registered: " + converterClass.getName());
        }
        return (Converter<Object, Object>) converter;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Converter<?, ?>> castConverterClass(Class<?> type) {
        return (Class<? extends Converter<?, ?>>) type;
    }
}
