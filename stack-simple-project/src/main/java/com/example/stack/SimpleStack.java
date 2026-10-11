package com.example.stack;

import java.util.EmptyStackException;

/** A small generic LIFO (last in, first out) stack. */
public class SimpleStack<T> {
    private Node<T> top;
    private int size;

    public void push(T value) {
        top = new Node<T>(value, top);
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

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
    }

    private static class Node<T> {
        private final T value;
        private final Node<T> next;

        private Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }
}
