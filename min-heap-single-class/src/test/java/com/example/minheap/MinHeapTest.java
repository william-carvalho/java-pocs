package com.example.minheap;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinHeapTest {
    @Test
    void insertsValuesAndKeepsMinimumAtRoot() {
        MinHeap<Integer> heap = new MinHeap<Integer>();

        heap.insert(10);
        heap.insert(4);
        heap.insert(25);
        heap.insert(1);

        assertEquals(Integer.valueOf(1), heap.peek());
        assertEquals(4, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void extractsValuesInAscendingOrder() {
        MinHeap<Integer> heap = new MinHeap<Integer>();
        heap.insert(3);
        heap.insert(10);
        heap.insert(1);
        heap.insert(7);

        assertEquals(Integer.valueOf(1), heap.extractMin());
        assertEquals(Integer.valueOf(3), heap.extractMin());
        assertEquals(Integer.valueOf(7), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void buildsHeapFromCollection() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(5, 1, 9, 3, 7));

        assertEquals(Integer.valueOf(1), heap.peek());
        assertEquals(5, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void replacesMinimumAndRestoresHeapOrder() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(2, 15, 10));

        assertEquals(Integer.valueOf(2), heap.replaceMin(20));

        assertEquals(Integer.valueOf(10), heap.peek());
        assertTrue(heap.isValidHeap());
        assertEquals(Arrays.asList(10, 15, 20), heap.toSortedAscendingList());
    }

    @Test
    void removesExistingValue() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(10, 30, 20, 5, 15));

        assertTrue(heap.remove(20));

        assertFalse(heap.contains(20));
        assertEquals(4, heap.size());
        assertTrue(heap.isValidHeap());
        assertEquals(Arrays.asList(5, 10, 15, 30), heap.toSortedAscendingList());
    }

    @Test
    void removeMissingValueReturnsFalse() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(1, 2, 3));

        assertFalse(heap.remove(99));
        assertEquals(3, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void supportsComparableStringValues() {
        MinHeap<String> heap = new MinHeap<String>();

        heap.insert("bravo");
        heap.insert("delta");
        heap.insert("alpha");

        assertEquals("alpha", heap.peek());
        assertEquals(Arrays.asList("alpha", "bravo", "delta"), heap.toSortedAscendingList());
    }

    @Test
    void sortedListDoesNotMutateHeap() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(4, 2, 8));

        assertEquals(Arrays.asList(2, 4, 8), heap.toSortedAscendingList());

        assertEquals(3, heap.size());
        assertEquals(Integer.valueOf(2), heap.peek());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void toListReturnsDefensiveCopy() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(4, 2, 8));

        heap.toList().clear();

        assertEquals(3, heap.size());
        assertEquals(Integer.valueOf(2), heap.peek());
    }

    @Test
    void clearResetsHeap() {
        MinHeap<Integer> heap = new MinHeap<Integer>(Arrays.asList(1, 2, 3));

        heap.clear();

        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertTrue(heap.toList().isEmpty());
        heap.insert(5);
        assertEquals(Integer.valueOf(5), heap.peek());
    }

    @Test
    void validatesInputsAndEmptyOperations() {
        MinHeap<Integer> heap = new MinHeap<Integer>();

        assertThrows(IllegalArgumentException.class, () -> new MinHeap<Integer>(null));
        assertThrows(IllegalArgumentException.class, () -> new MinHeap<Integer>(Arrays.asList(1, null)));
        assertThrows(IllegalArgumentException.class, () -> heap.insert(null));
        assertThrows(IllegalArgumentException.class, () -> heap.remove(null));
        assertThrows(IllegalArgumentException.class, () -> heap.contains(null));
        assertThrows(IllegalArgumentException.class, () -> heap.replaceMin(null));
        assertThrows(NoSuchElementException.class, heap::peek);
        assertThrows(NoSuchElementException.class, heap::extractMin);
        assertThrows(NoSuchElementException.class, () -> heap.replaceMin(1));
    }
}
