package com.example.heaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class HeapAlgorithms {
    private HeapAlgorithms() {
    }

    public static <T extends Comparable<? super T>> List<T> heapSort(
            Collection<? extends T> values) {
        Objects.requireNonNull(values, "values");
        BinaryHeap<T> heap = new BinaryHeap<T>(values, java.util.Comparator.naturalOrder());
        List<T> sorted = new ArrayList<T>(heap.size());
        while (!heap.isEmpty()) {
            sorted.add(heap.poll());
        }
        return sorted;
    }

    /** Retorna os k maiores valores em ordem decrescente usando O(k) memoria. */
    public static <T extends Comparable<? super T>> List<T> topK(
            Collection<? extends T> values, int k) {
        Objects.requireNonNull(values, "values");
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative");
        }
        if (k == 0) {
            return Collections.emptyList();
        }

        BinaryHeap<T> candidates = BinaryHeap.minHeap();
        for (T value : values) {
            Objects.requireNonNull(value, "values cannot contain null");
            if (candidates.size() < k) {
                candidates.offer(value);
            } else if (value.compareTo(candidates.peek()) > 0) {
                candidates.replaceRoot(value);
            }
        }

        List<T> result = new ArrayList<T>(candidates.size());
        while (!candidates.isEmpty()) {
            result.add(candidates.poll());
        }
        Collections.reverse(result);
        return result;
    }
}
