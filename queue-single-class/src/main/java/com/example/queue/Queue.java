package com.example.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Queue<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void enqueue(T value) {
        Node<T> node = new Node<T>(value);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public T dequeue() {
        ensureNotEmpty();
        T value = head.value;
        head = head.next;
        size--;
        if (isEmpty()) {
            tail = null;
        }
        return value;
    }

    public T peek() {
        ensureNotEmpty();
        return head.value;
    }

    public T front() {
        return peek();
    }

    public T back() {
        ensureNotEmpty();
        return tail.value;
    }

    public boolean contains(T value) {
        Node<T> current = head;
        while (current != null) {
            if (equalsValue(current.value, value)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public Object[] toArray() {
        Object[] values = new Object[size];
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            values[index] = current.value;
            current = current.next;
            index++;
        }
        return values;
    }

    public void clear() {
        head = null;
        tail = null;
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
            private Node<T> current = head;

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
            throw new NoSuchElementException("queue is empty");
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
