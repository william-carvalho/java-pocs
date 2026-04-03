package com.example.taxsystem.converter.application;

import com.example.taxsystem.common.annotation.FieldAlias;
import com.example.taxsystem.common.annotation.IgnoreField;
import com.example.taxsystem.common.annotation.Mappable;
import com.example.taxsystem.common.annotation.UseConverter;
import com.example.taxsystem.common.exception.ConversionException;
import com.example.taxsystem.common.reflection.ReflectionUtils;
import com.example.taxsystem.common.validation.ValidationSupport;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class ConverterEngine {

    private final ConverterRegistry converterRegistry;
    private final ValidationSupport validationSupport;

    public ConverterEngine(ConverterRegistry converterRegistry, ValidationSupport validationSupport) {
        this.converterRegistry = converterRegistry;
        this.validationSupport = validationSupport;
    }

    public <T> T convert(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        if (targetType.isAssignableFrom(source.getClass())) {
            return targetType.cast(source);
        }

        Optional<T> customResult = convertWithRegisteredConverter(source, targetType);
        if (customResult.isPresent()) {
            return customResult.get();
        }

        if (targetType.isEnum() && source instanceof String) {
            return convertEnum((String) source, targetType);
        }

        if (LocalDateTime.class.equals(targetType) && source instanceof String) {
            return convertWithRegisteredConverter(source, targetType)
                    .orElseThrow(() -> new ConversionException("No String to LocalDateTime converter registered"));
        }

        if (source instanceof Map || targetType.isAnnotationPresent(Mappable.class)) {
            return mapReflectively(source, targetType);
        }

        throw new ConversionException("No converter found for " + source.getClass().getName() + " -> " + targetType.getName());
    }

    public <T> List<T> convertCollection(Collection<?> source, Class<T> targetElementType) {
        List<T> results = new ArrayList<T>();
        if (source == null) {
            return results;
        }
        for (Object item : source) {
            results.add(convert(item, targetElementType));
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> convertWithRegisteredConverter(Object source, Class<T> targetType) {
        return converterRegistry.find(source.getClass(), targetType)
                .map(converter -> (T) converter.convert(source, this));
    }

    @SuppressWarnings("unchecked")
    private <T> T convertEnum(String value, Class<T> targetType) {
        return (T) Enum.valueOf((Class<? extends Enum>) targetType.asSubclass(Enum.class), value.trim().toUpperCase());
    }

    private <T> T mapReflectively(Object source, Class<T> targetType) {
        if (!targetType.isAnnotationPresent(Mappable.class)) {
            throw new ConversionException("Reflection mapping requires @Mappable on " + targetType.getName());
        }

        T target = instantiate(targetType);
        Predicate<Field> eligibleField = field -> !field.isAnnotationPresent(IgnoreField.class);

        for (Field targetField : ReflectionUtils.getAllFields(targetType)) {
            if (!eligibleField.test(targetField)) {
                continue;
            }
            resolveSourceValue(source, targetField).ifPresent(value -> ReflectionUtils.writeField(targetField, target, adaptValue(targetField, value)));
        }

        validationSupport.validate(target);
        return target;
    }

    private Optional<Object> resolveSourceValue(Object source, Field targetField) {
        String fieldName = targetField.isAnnotationPresent(FieldAlias.class)
                ? targetField.getAnnotation(FieldAlias.class).value()
                : targetField.getName();

        if (source instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) source;
            return Optional.ofNullable(map.get(fieldName));
        }

        return ReflectionUtils.findField(source.getClass(), fieldName)
                .map(field -> ReflectionUtils.readField(field, source));
    }

    private Object adaptValue(Field targetField, Object sourceValue) {
        if (sourceValue == null) {
            return null;
        }

        UseConverter useConverter = targetField.getAnnotation(UseConverter.class);
        if (useConverter != null) {
            return converterRegistry.getRequiredByClass(useConverter.value()).convert(sourceValue, this);
        }

        Class<?> targetFieldType = targetField.getType();
        if (Collection.class.isAssignableFrom(targetFieldType) && sourceValue instanceof Collection) {
            return convertNestedCollection(targetField, (Collection<?>) sourceValue);
        }

        if (Map.class.isAssignableFrom(targetFieldType) && sourceValue instanceof Map) {
            return sourceValue;
        }

        if (targetFieldType.isPrimitive()) {
            return adaptPrimitiveValue(targetFieldType, sourceValue);
        }

        if (targetFieldType.isAssignableFrom(sourceValue.getClass())) {
            return sourceValue;
        }

        return convert(sourceValue, targetFieldType);
    }

    private List<Object> convertNestedCollection(Field targetField, Collection<?> sourceCollection) {
        Class<?> targetElementType = resolveCollectionElementType(targetField);
        List<Object> results = new ArrayList<Object>();
        for (Object value : sourceCollection) {
            results.add(convert(value, targetElementType));
        }
        return results;
    }

    private Class<?> resolveCollectionElementType(Field targetField) {
        Type genericType = targetField.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new ConversionException("Collection field does not declare generic type: " + targetField.getName());
        }
        Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        if (!(actualType instanceof Class)) {
            throw new ConversionException("Unsupported generic element type on field " + targetField.getName());
        }
        return (Class<?>) actualType;
    }

    private <T> T instantiate(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
            throw new ConversionException("Cannot instantiate " + type.getName(), exception);
        }
    }

    private Object adaptPrimitiveValue(Class<?> primitiveType, Object sourceValue) {
        if (!(sourceValue instanceof Number) && !boolean.class.equals(primitiveType) && !char.class.equals(primitiveType)) {
            return sourceValue;
        }

        Number number = sourceValue instanceof Number ? (Number) sourceValue : null;
        if (int.class.equals(primitiveType)) {
            return number.intValue();
        }
        if (long.class.equals(primitiveType)) {
            return number.longValue();
        }
        if (double.class.equals(primitiveType)) {
            return number.doubleValue();
        }
        if (float.class.equals(primitiveType)) {
            return number.floatValue();
        }
        if (short.class.equals(primitiveType)) {
            return number.shortValue();
        }
        if (byte.class.equals(primitiveType)) {
            return number.byteValue();
        }
        if (boolean.class.equals(primitiveType)) {
            return Boolean.parseBoolean(String.valueOf(sourceValue));
        }
        if (char.class.equals(primitiveType)) {
            return String.valueOf(sourceValue).charAt(0);
        }
        return sourceValue;
    }
}
