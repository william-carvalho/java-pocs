package com.example.customstring.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomStringIterator implements Iterator<Character> {

    private final char[] value;
    private int index;

    public CustomStringIterator(char[] value) {
        this.value = value;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < value.length;
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more characters available");
        }
        return value[index++];
    }
}

