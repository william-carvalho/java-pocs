package com.example.customstring;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class CustomString implements Iterable<Character> {
    private final char[] chars;

    public CustomString(String value) {
        if (value == null) {
            this.chars = new char[0];
            return;
        }
        this.chars = new char[value.length()];
        for (int index = 0; index < value.length(); index++) {
            this.chars[index] = value.charAt(index);
        }
    }

    private CustomString(char[] chars) {
        this.chars = copy(chars);
    }

    public char[] toArray() {
        return copy(chars);
    }

    public void foreach(CharAction action) {
        if (action == null) {
            throw new IllegalArgumentException("action is required");
        }
        for (int index = 0; index < chars.length; index++) {
            action.accept(chars[index]);
        }
    }

    public CustomString reverse() {
        char[] reversed = new char[chars.length];
        for (int index = 0; index < chars.length; index++) {
            reversed[index] = chars[chars.length - 1 - index];
        }
        return new CustomString(reversed);
    }

    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private int index;

            public boolean hasNext() {
                return index < chars.length;
            }

            public Character next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return chars[index++];
            }

            public void remove() {
                throw new UnsupportedOperationException("CustomString is immutable");
            }
        };
    }

    public int length() {
        return chars.length;
    }

    public char charAt(int index) {
        if (index < 0 || index >= chars.length) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        return chars[index];
    }

    public boolean isEmpty() {
        return chars.length == 0;
    }

    public CustomString replace(char target, char replacement) {
        char[] replaced = new char[chars.length];
        for (int index = 0; index < chars.length; index++) {
            replaced[index] = chars[index] == target ? replacement : chars[index];
        }
        return new CustomString(replaced);
    }

    public CustomString substring(int beginIndex) {
        return substring(beginIndex, chars.length);
    }

    public CustomString substring(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > chars.length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("beginIndex: " + beginIndex + ", endIndex: " + endIndex);
        }
        char[] slice = new char[endIndex - beginIndex];
        for (int index = 0; index < slice.length; index++) {
            slice[index] = chars[beginIndex + index];
        }
        return new CustomString(slice);
    }

    public CustomString trim() {
        int start = 0;
        int end = chars.length - 1;
        while (start < chars.length && isWhitespace(chars[start])) {
            start++;
        }
        while (end >= start && isWhitespace(chars[end])) {
            end--;
        }
        return substring(start, end + 1);
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append('"');
        for (int index = 0; index < chars.length; index++) {
            char current = chars[index];
            if (current == '"') {
                json.append("\\\"");
            } else if (current == '\\') {
                json.append("\\\\");
            } else if (current == '\n') {
                json.append("\\n");
            } else if (current == '\r') {
                json.append("\\r");
            } else if (current == '\t') {
                json.append("\\t");
            } else {
                json.append(current);
            }
        }
        json.append('"');
        return json.toString();
    }

    public int indexOf(char target) {
        for (int index = 0; index < chars.length; index++) {
            if (chars[index] == target) {
                return index;
            }
        }
        return -1;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CustomString)) {
            return false;
        }
        CustomString that = (CustomString) other;
        if (chars.length != that.chars.length) {
            return false;
        }
        for (int index = 0; index < chars.length; index++) {
            if (chars[index] != that.chars[index]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = 0;
        for (int index = 0; index < chars.length; index++) {
            result = 31 * result + chars[index];
        }
        return result;
    }

    public String toString() {
        return new String(chars);
    }

    private static char[] copy(char[] source) {
        char[] copy = new char[source.length];
        for (int index = 0; index < source.length; index++) {
            copy[index] = source[index];
        }
        return copy;
    }

    private static boolean isWhitespace(char value) {
        return value == ' ' || value == '\n' || value == '\r' || value == '\t' || value == '\f';
    }

    public interface CharAction {
        void accept(char value);
    }
}
