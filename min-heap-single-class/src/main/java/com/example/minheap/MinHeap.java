package com.example.minheap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public final class MinHeap<T extends Comparable<T>> {
    private final List<T> values = new ArrayList<T>();

    public MinHeap() {
    }

    public MinHeap(Collection<T> initialValues) {
        if (initialValues == null) {
            throw new IllegalArgumentException("initialValues is required");
        }
        for (T value : initialValues) {
            validateValue(value);
            values.add(value);
        }
        heapify();
    }

    public void insert(T value) {
        validateValue(value);
        values.add(value);
        siftUp(values.size() - 1);
    }

    public T peek() {
        ensureNotEmpty();
        return values.get(0);
    }

    public T extractMin() {
        ensureNotEmpty();
        T min = values.get(0);
        T last = values.remove(values.size() - 1);
        if (!values.isEmpty()) {
            values.set(0, last);
            siftDown(0);
        }
        return min;
    }

    public T replaceMin(T value) {
        validateValue(value);
        ensureNotEmpty();
        T previous = values.set(0, value);
        siftDown(0);
        return previous;
    }

    public boolean remove(T value) {
        validateValue(value);
        int index = values.indexOf(value);
        if (index < 0) {
            return false;
        }
        removeAt(index);
        return true;
    }

    public boolean contains(T value) {
        validateValue(value);
        return values.contains(value);
    }

    public List<T> toList() {
        return new ArrayList<T>(values);
    }

    public List<T> toSortedAscendingList() {
        MinHeap<T> copy = new MinHeap<T>(values);
        List<T> sorted = new ArrayList<T>();
        while (!copy.isEmpty()) {
            sorted.add(copy.extractMin());
        }
        return sorted;
    }

    public boolean isValidHeap() {
        for (int index = 0; index < values.size(); index++) {
            int left = leftChild(index);
            int right = rightChild(index);
            if (left < values.size() && compare(values.get(index), values.get(left)) > 0) {
                return false;
            }
            if (right < values.size() && compare(values.get(index), values.get(right)) > 0) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void clear() {
        values.clear();
    }

    private T removeAt(int index) {
        T removed = values.get(index);
        T last = values.remove(values.size() - 1);
        if (index < values.size()) {
            values.set(index, last);
            int parent = parent(index);
            if (index > 0 && compare(values.get(index), values.get(parent)) < 0) {
                siftUp(index);
            } else {
                siftDown(index);
            }
        }
        return removed;
    }

    private void heapify() {
        for (int index = parent(values.size() - 1); index >= 0; index--) {
            siftDown(index);
        }
    }

    private void siftUp(int index) {
        int current = index;
        while (current > 0) {
            int parent = parent(current);
            if (compare(values.get(current), values.get(parent)) >= 0) {
                return;
            }
            swap(current, parent);
            current = parent;
        }
    }

    private void siftDown(int index) {
        int current = index;
        while (true) {
            int left = leftChild(current);
            int right = rightChild(current);
            int smallest = current;

            if (left < values.size() && compare(values.get(left), values.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < values.size() && compare(values.get(right), values.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == current) {
                return;
            }
            swap(current, smallest);
            current = smallest;
        }
    }

    private void swap(int left, int right) {
        T temporary = values.get(left);
        values.set(left, values.get(right));
        values.set(right, temporary);
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int leftChild(int index) {
        return index * 2 + 1;
    }

    private int rightChild(int index) {
        return index * 2 + 2;
    }

    private int compare(T left, T right) {
        return left.compareTo(right);
    }

    private void validateValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
    }
}
