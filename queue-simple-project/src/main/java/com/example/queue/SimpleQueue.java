package com.example.queue;

import java.util.NoSuchElementException;

/** A small generic FIFO (first in, first out) queue. */
public class SimpleQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public void enqueue(T value) {
        Node<T> newNode = new Node<T>(value);

        if (isEmpty()) {
            front = newNode;
        } else {
            rear.next = newNode;
        }

        rear = newNode;
        size++;
    }

    public T dequeue() {
        ensureNotEmpty();
        T value = front.value;
        front = front.next;
        size--;

        if (isEmpty()) {
            rear = null;
        }

        return value;
    }

    public T peek() {
        ensureNotEmpty();
        return front.value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
    }

    private static class Node<T> {
        private final T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
