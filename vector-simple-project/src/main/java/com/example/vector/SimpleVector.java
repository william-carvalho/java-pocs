package com.example.vector;

import java.util.Arrays;

/** A small generic dynamic array, similar to ArrayList or Vector. */
public class SimpleVector<T> {
    private static final int DEFAULT_CAPACITY = 4;

    private Object[] elements;
    private int size;

    public SimpleVector() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public void add(T value) {
        ensureCapacity();
        elements[size] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T value) {
        checkIndex(index);
        T previous = (T) elements[index];
        elements[index] = value;
        return previous;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) elements[index];

        int valuesToMove = size - index - 1;
        if (valuesToMove > 0) {
            System.arraycopy(elements, index + 1, elements, index, valuesToMove);
        }

        elements[--size] = null;
        return removed;
    }

    public boolean contains(T value) {
        for (int index = 0; index < size; index++) {
            if (value == null ? elements[index] == null : value.equals(elements[index])) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is outside vector size " + size);
        }
    }
}
