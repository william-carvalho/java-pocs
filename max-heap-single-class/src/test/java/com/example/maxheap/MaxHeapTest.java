package com.example.maxheap;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaxHeapTest {
    @Test
    void insertsValuesAndKeepsMaximumAtRoot() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>();

        heap.insert(10);
        heap.insert(4);
        heap.insert(25);
        heap.insert(17);

        assertEquals(Integer.valueOf(25), heap.peek());
        assertEquals(4, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void extractsValuesInDescendingOrder() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>();
        heap.insert(3);
        heap.insert(10);
        heap.insert(1);
        heap.insert(7);

        assertEquals(Integer.valueOf(10), heap.extractMax());
        assertEquals(Integer.valueOf(7), heap.extractMax());
        assertEquals(Integer.valueOf(3), heap.extractMax());
        assertEquals(Integer.valueOf(1), heap.extractMax());
        assertTrue(heap.isEmpty());
    }

    @Test
    void buildsHeapFromCollection() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(5, 1, 9, 3, 7));

        assertEquals(Integer.valueOf(9), heap.peek());
        assertEquals(5, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void replacesMaximumAndRestoresHeapOrder() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(20, 15, 10));

        assertEquals(Integer.valueOf(20), heap.replaceMax(8));

        assertEquals(Integer.valueOf(15), heap.peek());
        assertTrue(heap.isValidHeap());
        assertEquals(Arrays.asList(15, 10, 8), heap.toSortedDescendingList());
    }

    @Test
    void removesExistingValue() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(10, 30, 20, 5, 15));

        assertTrue(heap.remove(20));

        assertFalse(heap.contains(20));
        assertEquals(4, heap.size());
        assertTrue(heap.isValidHeap());
        assertEquals(Arrays.asList(30, 15, 10, 5), heap.toSortedDescendingList());
    }

    @Test
    void removeMissingValueReturnsFalse() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(1, 2, 3));

        assertFalse(heap.remove(99));
        assertEquals(3, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void supportsComparableStringValues() {
        MaxHeap<String> heap = new MaxHeap<String>();

        heap.insert("bravo");
        heap.insert("delta");
        heap.insert("alpha");

        assertEquals("delta", heap.peek());
        assertEquals(Arrays.asList("delta", "bravo", "alpha"), heap.toSortedDescendingList());
    }

    @Test
    void sortedListDoesNotMutateHeap() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(4, 2, 8));

        assertEquals(Arrays.asList(8, 4, 2), heap.toSortedDescendingList());

        assertEquals(3, heap.size());
        assertEquals(Integer.valueOf(8), heap.peek());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void toListReturnsDefensiveCopy() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(4, 2, 8));

        heap.toList().clear();

        assertEquals(3, heap.size());
        assertEquals(Integer.valueOf(8), heap.peek());
    }

    @Test
    void clearResetsHeap() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>(Arrays.asList(1, 2, 3));

        heap.clear();

        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertTrue(heap.toList().isEmpty());
        heap.insert(5);
        assertEquals(Integer.valueOf(5), heap.peek());
    }

    @Test
    void validatesInputsAndEmptyOperations() {
        MaxHeap<Integer> heap = new MaxHeap<Integer>();

        assertThrows(IllegalArgumentException.class, () -> new MaxHeap<Integer>(null));
        assertThrows(IllegalArgumentException.class, () -> new MaxHeap<Integer>(Arrays.asList(1, null)));
        assertThrows(IllegalArgumentException.class, () -> heap.insert(null));
        assertThrows(IllegalArgumentException.class, () -> heap.remove(null));
        assertThrows(IllegalArgumentException.class, () -> heap.contains(null));
        assertThrows(IllegalArgumentException.class, () -> heap.replaceMax(null));
        assertThrows(NoSuchElementException.class, heap::peek);
        assertThrows(NoSuchElementException.class, heap::extractMax);
        assertThrows(NoSuchElementException.class, () -> heap.replaceMax(1));
    }
}
