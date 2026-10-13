package com.example.heaps;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinaryHeapTest {
    @Test
    void shouldMaintainMinHeapProperty() {
        BinaryHeap<Integer> heap = new BinaryHeap<Integer>(
                Arrays.asList(9, 4, 7, 1, 8, 2, 6), Comparator.naturalOrder());

        assertTrue(heap.isValid());
        assertEquals(1, heap.peek());
        assertEquals(Arrays.asList(1, 2, 4, 6, 7, 8, 9), drain(heap));
    }

    @Test
    void shouldMaintainMaxHeapAndSupportRemovalAndReplacement() {
        BinaryHeap<Integer> heap = BinaryHeap.maxHeap();
        Arrays.asList(5, 1, 9, 3, 7).forEach(heap::offer);

        assertTrue(heap.remove(3));
        assertFalse(heap.remove(99));
        assertEquals(9, heap.replaceRoot(4));
        assertTrue(heap.isValid());
        assertEquals(Arrays.asList(7, 5, 4, 1), drain(heap));
    }

    @Test
    void shouldRejectInvalidOperations() {
        BinaryHeap<Integer> heap = BinaryHeap.minHeap();

        assertThrows(NoSuchElementException.class, heap::peek);
        assertThrows(NoSuchElementException.class, heap::poll);
        assertThrows(NullPointerException.class, () -> heap.offer(null));
    }

    private java.util.List<Integer> drain(BinaryHeap<Integer> heap) {
        java.util.List<Integer> result = new java.util.ArrayList<Integer>();
        while (!heap.isEmpty()) {
            result.add(heap.poll());
        }
        return result;
    }
}
