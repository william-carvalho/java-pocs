package com.example.doublylinkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DoublyLinkedList<T> implements Iterable<T> {
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

        Node<T> current = nodeAt(index);
        Node<T> node = new Node<T>(value);
        Node<T> previous = current.previous;
        node.previous = previous;
        node.next = current;
        previous.next = node;
        current.previous = node;
        size++;
    }

    public T get(int index) {
        validateIndex(index);
        return nodeAt(index).value;
    }

    public T set(int index, T value) {
        validateIndex(index);
        Node<T> node = nodeAt(index);
        T previous = node.value;
        node.value = value;
        return previous;
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

    public T removeAt(int index) {
        validateIndex(index);
        if (index == 0) {
            return removeFirst();
        }
        if (index == size - 1) {
            return removeLast();
        }

        Node<T> node = nodeAt(index);
        unlink(node);
        return node.value;
    }

    public boolean remove(T value) {
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

    public boolean contains(T value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(T value) {
        Node<T> current = head;
        int index = 0;
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
        Node<T> current = head;
        Node<T> oldHead = head;
        while (current != null) {
            Node<T> next = current.next;
            current.next = current.previous;
            current.previous = next;
            current = next;
        }
        head = tail;
        tail = oldHead;
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

    public Object[] toReverseArray() {
        Object[] values = new Object[size];
        Node<T> current = tail;
        int index = 0;
        while (current != null) {
            values[index] = current.value;
            current = current.previous;
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

    public Iterator<T> reverseIterator() {
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

    private Node<T> nodeAt(int index) {
        if (index < size / 2) {
            Node<T> current = head;
            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
                current = current.next;
            }
            return current;
        }

        Node<T> current = tail;
        for (int currentIndex = size - 1; currentIndex > index; currentIndex--) {
            current = current.previous;
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
        private T value;
        private Node<T> previous;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
