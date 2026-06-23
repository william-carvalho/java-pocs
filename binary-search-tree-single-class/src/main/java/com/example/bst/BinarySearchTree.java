package com.example.bst;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class BinarySearchTree<T extends Comparable<T>> {
    private Node<T> root;
    private int size;

    public boolean insert(T value) {
        validateValue(value);
        if (root == null) {
            root = new Node<T>(value);
            size++;
            return true;
        }

        Node<T> current = root;
        while (true) {
            int comparison = value.compareTo(current.value);
            if (comparison == 0) {
                return false;
            }
            if (comparison < 0) {
                if (current.left == null) {
                    current.left = new Node<T>(value);
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<T>(value);
                    size++;
                    return true;
                }
                current = current.right;
            }
        }
    }

    public boolean contains(T value) {
        validateValue(value);
        return find(root, value) != null;
    }

    public boolean remove(T value) {
        validateValue(value);
        if (!contains(value)) {
            return false;
        }
        root = remove(root, value);
        size--;
        return true;
    }

    public T min() {
        ensureNotEmpty();
        return minNode(root).value;
    }

    public T max() {
        ensureNotEmpty();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public List<T> inOrder() {
        List<T> values = new ArrayList<T>();
        inOrder(root, values);
        return values;
    }

    public List<T> preOrder() {
        List<T> values = new ArrayList<T>();
        preOrder(root, values);
        return values;
    }

    public List<T> postOrder() {
        List<T> values = new ArrayList<T>();
        postOrder(root, values);
        return values;
    }

    public int height() {
        return height(root);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    private Node<T> remove(Node<T> current, T value) {
        if (current == null) {
            return null;
        }

        int comparison = value.compareTo(current.value);
        if (comparison < 0) {
            current.left = remove(current.left, value);
            return current;
        }
        if (comparison > 0) {
            current.right = remove(current.right, value);
            return current;
        }

        if (current.left == null) {
            return current.right;
        }
        if (current.right == null) {
            return current.left;
        }

        Node<T> successor = minNode(current.right);
        current.value = successor.value;
        current.right = remove(current.right, successor.value);
        return current;
    }

    private Node<T> find(Node<T> current, T value) {
        while (current != null) {
            int comparison = value.compareTo(current.value);
            if (comparison == 0) {
                return current;
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return null;
    }

    private Node<T> minNode(Node<T> current) {
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private void inOrder(Node<T> current, List<T> values) {
        if (current == null) {
            return;
        }
        inOrder(current.left, values);
        values.add(current.value);
        inOrder(current.right, values);
    }

    private void preOrder(Node<T> current, List<T> values) {
        if (current == null) {
            return;
        }
        values.add(current.value);
        preOrder(current.left, values);
        preOrder(current.right, values);
    }

    private void postOrder(Node<T> current, List<T> values) {
        if (current == null) {
            return;
        }
        postOrder(current.left, values);
        postOrder(current.right, values);
        values.add(current.value);
    }

    private int height(Node<T> current) {
        if (current == null) {
            return 0;
        }
        return 1 + Math.max(height(current.left), height(current.right));
    }

    private void validateValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("tree is empty");
        }
    }

    private static final class Node<T> {
        private T value;
        private Node<T> left;
        private Node<T> right;

        private Node(T value) {
            this.value = value;
        }
    }
}
