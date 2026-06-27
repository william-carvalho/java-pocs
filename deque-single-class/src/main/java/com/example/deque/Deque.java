package com.example.deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Deque<T> implements Iterable<T> {
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
            head.previous = node;
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
            node.previous = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public T removeFirst() {
        ensureNotEmpty();
        T value = head.value;
        head = head.next;
        size--;
        if (isEmpty()) {
            tail = null;
        } else {
            head.previous = null;
        }
        return value;
    }

    public T removeLast() {
        ensureNotEmpty();
        T value = tail.value;
        tail = tail.previous;
        size--;
        if (isEmpty()) {
            head = null;
        } else {
            tail.next = null;
        }
        return value;
    }

    public T peekFirst() {
        ensureNotEmpty();
        return head.value;
    }

    public T peekLast() {
        ensureNotEmpty();
        return tail.value;
    }

    public boolean removeFirstOccurrence(T value) {
        Node<T> current = head;
        while (current != null) {
            if (equalsValue(current.value, value)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean removeLastOccurrence(T value) {
        Node<T> current = tail;
        while (current != null) {
            if (equalsValue(current.value, value)) {
                unlink(current);
                return true;
            }
            current = current.previous;
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

    public Iterator<T> descendingIterator() {
        return new Iterator<T>() {
            private Node<T> current = tail;

            public boolean hasNext() {
                return current != null;
            }

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("no more elements");
                }
                T value = current.value;
                current = current.previous;
                return value;
            }

            public void remove() {
                throw new UnsupportedOperationException("remove is not supported");
            }
        };
    }

    private void unlink(Node<T> node) {
        if (node == head) {
            removeFirst();
            return;
        }
        if (node == tail) {
            removeLast();
            return;
        }
        node.previous.next = node.next;
        node.next.previous = node.previous;
        size--;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("deque is empty");
        }
    }

    private boolean equalsValue(T left, T right) {
        return left == null ? right == null : left.equals(right);
    }

    private static final class Node<T> {
        private final T value;
        private Node<T> previous;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
