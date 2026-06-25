package com.example.circularqueue;

import java.util.NoSuchElementException;

public final class CircularQueue<T> {
    private final Object[] elements;
    private int head;
    private int tail;
    private int size;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        this.elements = new Object[capacity];
    }

    public void enqueue(T value) {
        if (isFull()) {
            throw new IllegalStateException("queue is full");
        }
        elements[tail] = value;
        tail = nextIndex(tail);
        size++;
    }

    public T dequeue() {
        ensureNotEmpty();
        T value = valueAt(head);
        elements[head] = null;
        head = nextIndex(head);
        size--;
        if (size == 0) {
            head = 0;
            tail = 0;
        }
        return value;
    }

    public T peek() {
        ensureNotEmpty();
        return valueAt(head);
    }

    public Object[] toArray() {
        Object[] values = new Object[size];
        for (int index = 0; index < size; index++) {
            values[index] = elements[(head + index) % elements.length];
        }
        return values;
    }

    public void clear() {
        for (int index = 0; index < elements.length; index++) {
            elements[index] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    public int remainingCapacity() {
        return capacity() - size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == elements.length;
    }

    private int nextIndex(int index) {
        return (index + 1) % elements.length;
    }

    @SuppressWarnings("unchecked")
    private T valueAt(int index) {
        return (T) elements[index];
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
    }
}
