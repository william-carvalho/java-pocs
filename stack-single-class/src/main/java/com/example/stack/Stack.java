package com.example.stack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Stack<T> implements Iterable<T> {
    private Node<T> top;
    private int size;

    public void push(T value) {
        Node<T> node = new Node<T>(value);
        node.next = top;
        top = node;
        size++;
    }

    public T pop() {
        ensureNotEmpty();
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    public T peek() {
        ensureNotEmpty();
        return top.value;
    }

    public boolean contains(T value) {
        return search(value) >= 0;
    }

    public int search(T value) {
        Node<T> current = top;
        int distanceFromTop = 0;
        while (current != null) {
            if (equalsValue(current.value, value)) {
                return distanceFromTop;
            }
            current = current.next;
            distanceFromTop++;
        }
        return -1;
    }

    public Object[] toArray() {
        Object[] values = new Object[size];
        Node<T> current = top;
        int index = 0;
        while (current != null) {
            values[index] = current.value;
            current = current.next;
            index++;
        }
        return values;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = top;

            public boolean hasNext() {
                return current != null;
            }

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("no more elements");
                }
                T value = current.value;
                current = current.next;
                return value;
            }

            public void remove() {
                throw new UnsupportedOperationException("remove is not supported");
            }
        };
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("stack is empty");
        }
    }

    private boolean equalsValue(T left, T right) {
        return left == null ? right == null : left.equals(right);
    }

    private static final class Node<T> {
        private final T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
