package com.example.binarysearch;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinarySearchTest {
    @Test
    void findsExistingIntegerValues() {
        int[] values = new int[]{1, 3, 5, 7, 9};

        assertEquals(0, BinarySearch.search(values, 1));
        assertEquals(2, BinarySearch.search(values, 5));
        assertEquals(4, BinarySearch.search(values, 9));
    }

    @Test
    void returnsMinusOneWhenIntegerValueIsMissing() {
        int[] values = new int[]{1, 3, 5, 7, 9};

        assertEquals(-1, BinarySearch.search(values, 0));
        assertEquals(-1, BinarySearch.search(values, 4));
        assertEquals(-1, BinarySearch.search(values, 10));
    }

    @Test
    void handlesEmptyAndSingleElementArrays() {
        assertEquals(-1, BinarySearch.search(new int[0], 1));
        assertEquals(0, BinarySearch.search(new int[]{5}, 5));
        assertEquals(-1, BinarySearch.search(new int[]{5}, 4));
    }

    @Test
    void findsFirstAndLastIndexWhenDuplicatesExist() {
        int[] values = new int[]{1, 2, 2, 2, 3, 4};

        assertEquals(1, BinarySearch.firstIndexOf(values, 2));
        assertEquals(3, BinarySearch.lastIndexOf(values, 2));
        assertEquals(-1, BinarySearch.firstIndexOf(values, 9));
        assertEquals(-1, BinarySearch.lastIndexOf(values, 9));
    }

    @Test
    void calculatesLowerBound() {
        int[] values = new int[]{1, 2, 2, 4, 7};

        assertEquals(0, BinarySearch.lowerBound(values, 0));
        assertEquals(1, BinarySearch.lowerBound(values, 2));
        assertEquals(3, BinarySearch.lowerBound(values, 3));
        assertEquals(5, BinarySearch.lowerBound(values, 9));
    }

    @Test
    void calculatesUpperBound() {
        int[] values = new int[]{1, 2, 2, 4, 7};

        assertEquals(0, BinarySearch.upperBound(values, 0));
        assertEquals(3, BinarySearch.upperBound(values, 2));
        assertEquals(3, BinarySearch.upperBound(values, 3));
        assertEquals(5, BinarySearch.upperBound(values, 9));
    }

    @Test
    void supportsNegativeNumbers() {
        int[] values = new int[]{-10, -3, 0, 4, 12};

        assertEquals(1, BinarySearch.search(values, -3));
        assertEquals(0, BinarySearch.lowerBound(values, -10));
        assertEquals(2, BinarySearch.upperBound(values, -3));
    }

    @Test
    void searchesComparableLists() {
        assertEquals(1, BinarySearch.search(Arrays.asList("alpha", "bravo", "charlie"), "bravo"));
        assertEquals(-1, BinarySearch.search(Arrays.asList("alpha", "bravo", "charlie"), "delta"));
        assertEquals(0, BinarySearch.search(Collections.singletonList("only"), "only"));
    }

    @Test
    void validatesIntegerArrayInputs() {
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.search(null, 1));
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.firstIndexOf(null, 1));
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.lastIndexOf(null, 1));
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.lowerBound(null, 1));
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.upperBound(null, 1));
    }

    @Test
    void validatesListInputs() {
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.search(null, "a"));
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.search(Arrays.asList("a"), null));
        assertThrows(IllegalArgumentException.class, () -> BinarySearch.search(Arrays.asList("a", null), "a"));
    }
}
