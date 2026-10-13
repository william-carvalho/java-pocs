package com.example.heaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Heap binario generico. O Comparator define qual elemento tem maior prioridade.
 */
public final class BinaryHeap<T> {
    private final List<T> elements;
    private final Comparator<? super T> priorityComparator;

    public BinaryHeap(Comparator<? super T> priorityComparator) {
        this.elements = new ArrayList<T>();
        this.priorityComparator = Objects.requireNonNull(priorityComparator, "priorityComparator");
    }

    public BinaryHeap(Collection<? extends T> values,
                      Comparator<? super T> priorityComparator) {
        this(priorityComparator);
        Objects.requireNonNull(values, "values");
        for (T value : values) {
            elements.add(requireValue(value));
        }
        heapify();
    }

    public static <T extends Comparable<? super T>> BinaryHeap<T> minHeap() {
        return new BinaryHeap<T>(Comparator.naturalOrder());
    }

    public static <T extends Comparable<? super T>> BinaryHeap<T> maxHeap() {
        return new BinaryHeap<T>(Comparator.reverseOrder());
    }

    public void offer(T value) {
        elements.add(requireValue(value));
        siftUp(elements.size() - 1);
    }

    public T peek() {
        ensureNotEmpty();
        return elements.get(0);
    }

    public T poll() {
        ensureNotEmpty();
        T root = elements.get(0);
        T last = elements.remove(elements.size() - 1);
        if (!elements.isEmpty()) {
            elements.set(0, last);
            siftDown(0);
        }
        return root;
    }

    public T replaceRoot(T value) {
        ensureNotEmpty();
        T previous = elements.set(0, requireValue(value));
        siftDown(0);
        return previous;
    }

    public boolean remove(T value) {
        requireValue(value);
        int index = elements.indexOf(value);
        if (index < 0) {
            return false;
        }

        T last = elements.remove(elements.size() - 1);
        if (index < elements.size()) {
            elements.set(index, last);
            if (index > 0 && hasHigherPriority(elements.get(index), elements.get(parent(index)))) {
                siftUp(index);
            } else {
                siftDown(index);
            }
        }
        return true;
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public List<T> toLevelOrderList() {
        return new ArrayList<T>(elements);
    }

    public boolean isValid() {
        for (int child = 1; child < elements.size(); child++) {
            if (hasHigherPriority(elements.get(child), elements.get(parent(child)))) {
                return false;
            }
        }
        return true;
    }

    private void heapify() {
        for (int index = elements.size() / 2 - 1; index >= 0; index--) {
            siftDown(index);
        }
    }

    private void siftUp(int start) {
        int child = start;
        while (child > 0) {
            int parent = parent(child);
            if (!hasHigherPriority(elements.get(child), elements.get(parent))) {
                return;
            }
            swap(child, parent);
            child = parent;
        }
    }

    private void siftDown(int start) {
        int parent = start;
        while (leftChild(parent) < elements.size()) {
            int bestChild = leftChild(parent);
            int right = rightChild(parent);
            if (right < elements.size()
                    && hasHigherPriority(elements.get(right), elements.get(bestChild))) {
                bestChild = right;
            }
            if (!hasHigherPriority(elements.get(bestChild), elements.get(parent))) {
                return;
            }
            swap(parent, bestChild);
            parent = bestChild;
        }
    }

    private boolean hasHigherPriority(T left, T right) {
        return priorityComparator.compare(left, right) < 0;
    }

    private T requireValue(T value) {
        return Objects.requireNonNull(value, "value");
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
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

    private void swap(int first, int second) {
        T temporary = elements.get(first);
        elements.set(first, elements.get(second));
        elements.set(second, temporary);
    }
}
