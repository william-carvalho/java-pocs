package com.example.converterframework.registry;

import java.util.Objects;

public class ConverterKey {

    private final Class<?> sourceType;
    private final Class<?> targetType;

    public ConverterKey(Class<?> sourceType, Class<?> targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConverterKey)) {
            return false;
        }
        ConverterKey that = (ConverterKey) o;
        return Objects.equals(sourceType, that.sourceType) && Objects.equals(targetType, that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceType, targetType);
    }
}

