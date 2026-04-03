package com.example.taxsystem.common.reflection;

import com.example.taxsystem.common.exception.ConversionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> current = type;
        while (current != null && !Object.class.equals(current)) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    public static Optional<Field> findField(Class<?> type, String name) {
        return getAllFields(type).stream()
                .filter(field -> field.getName().equals(name))
                .findFirst();
    }

    public static Object readField(Field field, Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException exception) {
            throw new ConversionException("Cannot read field " + field.getName(), exception);
        }
    }

    public static void writeField(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException exception) {
            throw new ConversionException("Cannot write field " + field.getName(), exception);
        }
    }

    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new ConversionException("Cannot invoke method " + method.getName(), exception);
        }
    }
}
