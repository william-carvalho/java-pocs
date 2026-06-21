package com.example.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T value) {
        Node<T> node = new Node<T>(value);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head = node;
        }
        size++;
    }

    public void addLast(T value) {
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

    public void insertAt(int index, T value) {
        validatePosition(index);
        if (index == 0) {
            addFirst(value);
            return;
        }
        if (index == size) {
            addLast(value);
            return;
        }

        Node<T> previous = nodeAt(index - 1);
        Node<T> node = new Node<T>(value);
        node.next = previous.next;
        previous.next = node;
        size++;
    }

    public T get(int index) {
        validateIndex(index);
        return nodeAt(index).value;
    }

    public T removeFirst() {
        ensureNotEmpty();
        T removed = head.value;
        head = head.next;
        size--;
        if (size == 0) {
            tail = null;
        }
        return removed;
    }

    public T removeLast() {
        ensureNotEmpty();
        if (size == 1) {
            return removeFirst();
        }

        Node<T> previous = nodeAt(size - 2);
        T removed = tail.value;
        previous.next = null;
        tail = previous;
        size--;
        return removed;
    }

    public T removeAt(int index) {
        validateIndex(index);
        if (index == 0) {
            return removeFirst();
        }
        if (index == size - 1) {
            return removeLast();
        }

        Node<T> previous = nodeAt(index - 1);
        Node<T> removed = previous.next;
        previous.next = removed.next;
        size--;
        return removed.value;
    }

    public boolean remove(T value) {
        if (isEmpty()) {
            return false;
        }
        if (equalsValue(head.value, value)) {
            removeFirst();
            return true;
        }

        Node<T> previous = head;
        Node<T> current = head.next;
        while (current != null) {
            if (equalsValue(current.value, value)) {
                previous.next = current.next;
                if (current == tail) {
                    tail = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
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

    public int indexOf(T value) {
        int index = 0;
        Node<T> current = head;
        while (current != null) {
            if (equalsValue(current.value, value)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    public void reverse() {
        Node<T> previous = null;
        Node<T> current = head;
        tail = head;
        while (current != null) {
            Node<T> next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        head = previous;
    }

    public Object[] toArray() {
        Object[] values = new Object[size];
        int index = 0;
        Node<T> current = head;
        while (current != null) {
            values[index] = current.value;
            current = current.next;
            index++;
        }
        return values;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
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

    private Node<T> nodeAt(int index) {
        Node<T> current = head;
        for (int currentIndex = 0; currentIndex < index; currentIndex++) {
            current = current.next;
        }
        return current;
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index);
        }
    }

    private void validatePosition(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("invalid position: " + index);
        }
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("list is empty");
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
