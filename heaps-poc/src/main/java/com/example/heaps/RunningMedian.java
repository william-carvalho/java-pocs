package com.example.heaps;

import java.util.NoSuchElementException;

/** Calcula a mediana de um fluxo usando um max-heap e um min-heap. */
public final class RunningMedian {
    private final BinaryHeap<Integer> lowerHalf = BinaryHeap.maxHeap();
    private final BinaryHeap<Integer> upperHalf = BinaryHeap.minHeap();

    public void add(int value) {
        if (lowerHalf.isEmpty() || value <= lowerHalf.peek()) {
            lowerHalf.offer(value);
        } else {
            upperHalf.offer(value);
        }
        rebalance();
    }

    public double median() {
        if (size() == 0) {
            throw new NoSuchElementException("no values were added");
        }
        if (lowerHalf.size() == upperHalf.size()) {
            return ((long) lowerHalf.peek() + upperHalf.peek()) / 2.0;
        }
        return lowerHalf.peek();
    }

    public int size() {
        return lowerHalf.size() + upperHalf.size();
    }

    private void rebalance() {
        if (lowerHalf.size() > upperHalf.size() + 1) {
            upperHalf.offer(lowerHalf.poll());
        } else if (upperHalf.size() > lowerHalf.size()) {
            lowerHalf.offer(upperHalf.poll());
        }
    }
}
