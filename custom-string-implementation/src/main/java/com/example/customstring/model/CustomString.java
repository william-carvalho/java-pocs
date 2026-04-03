package com.example.customstring.model;

import com.example.customstring.iterator.CustomStringIterator;
import com.example.customstring.util.JsonEscapeUtil;

import java.util.Iterator;
import java.util.function.Consumer;

public final class CustomString implements Iterable<Character> {

    private final char[] value;

    public CustomString(String input) {
        this(input == null ? new char[0] : input.toCharArray());
    }

    public CustomString(char[] input) {
        if (input == null) {
            this.value = new char[0];
            return;
        }
        this.value = copy(input);
    }

    public char[] toArray() {
        return copy(value);
    }

    public void forEachChar(Consumer<Character> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer must not be null");
        }
        for (char current : value) {
            consumer.accept(current);
        }
    }

    public CustomString reverse() {
        char[] reversed = new char[value.length];
        for (int index = 0; index < value.length; index++) {
            reversed[index] = value[value.length - 1 - index];
        }
        return new CustomString(reversed);
    }

    public int length() {
        return value.length;
    }

    public char charAt(int index) {
        validateIndex(index);
        return value[index];
    }

    public boolean isEmpty() {
        return value.length == 0;
    }

    public CustomString replace(char oldChar, char newChar) {
        char[] replaced = new char[value.length];
        for (int index = 0; index < value.length; index++) {
            replaced[index] = value[index] == oldChar ? newChar : value[index];
        }
        return new CustomString(replaced);
    }

    public CustomString substring(int beginIndex) {
        return substring(beginIndex, value.length);
    }

    public CustomString substring(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex < beginIndex || endIndex > value.length) {
            throw new IndexOutOfBoundsException("Invalid substring range: " + beginIndex + " to " + endIndex);
        }
        char[] result = new char[endIndex - beginIndex];
        for (int index = beginIndex; index < endIndex; index++) {
            result[index - beginIndex] = value[index];
        }
        return new CustomString(result);
    }

    public CustomString trim() {
        if (isEmpty()) {
            return new CustomString(value);
        }

        int start = 0;
        int end = value.length - 1;

        while (start <= end && Character.isWhitespace(value[start])) {
            start++;
        }
        while (end >= start && Character.isWhitespace(value[end])) {
            end--;
        }

        if (start == 0 && end == value.length - 1) {
            return new CustomString(value);
        }
        if (start > end) {
            return new CustomString("");
        }
        return substring(start, end + 1);
    }

    public String toJson() {
        return JsonEscapeUtil.escape(value);
    }

    public int indexOf(char ch) {
        for (int index = 0; index < value.length; index++) {
            if (value[index] == ch) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public Iterator<Character> iterator() {
        return new CustomStringIterator(value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CustomString)) {
            return false;
        }

        CustomString other = (CustomString) object;
        if (value.length != other.value.length) {
            return false;
        }
        for (int index = 0; index < value.length; index++) {
            if (value[index] != other.value[index]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (char current : value) {
            hash = 31 * hash + current;
        }
        return hash;
    }

    @Override
    public String toString() {
        return new String(value);
    }

    private char[] copy(char[] source) {
        char[] copy = new char[source.length];
        for (int index = 0; index < source.length; index++) {
            copy[index] = source[index];
        }
        return copy;
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= value.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }
}

