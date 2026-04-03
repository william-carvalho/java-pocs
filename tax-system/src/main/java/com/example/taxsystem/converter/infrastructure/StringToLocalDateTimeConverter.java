package com.example.taxsystem.converter.infrastructure;

import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.converter.domain.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public Class<String> sourceType() {
        return String.class;
    }

    @Override
    public Class<LocalDateTime> targetType() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime convert(String source, ConverterEngine converterEngine) {
        return LocalDateTime.parse(source);
    }
}
