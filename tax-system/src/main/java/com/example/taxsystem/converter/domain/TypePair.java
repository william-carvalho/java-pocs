package com.example.taxsystem.converter.domain;

import java.util.Objects;

public class TypePair {

    private final Class<?> sourceType;
    private final Class<?> targetType;

    public TypePair(Class<?> sourceType, Class<?> targetType) {
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TypePair)) {
            return false;
        }
        TypePair that = (TypePair) other;
        return Objects.equals(sourceType, that.sourceType) && Objects.equals(targetType, that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceType, targetType);
    }
}
