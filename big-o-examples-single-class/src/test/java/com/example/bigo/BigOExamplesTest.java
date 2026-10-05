package com.example.bigo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BigOExamplesTest {
    @Test
    void constantAccessAlwaysUsesOneOperation() {
        assertEquals(1, BigOExamples.first(new int[]{10}).getOperations());
        assertEquals(1, BigOExamples.first(new int[]{10, 20, 30, 40}).getOperations());
        assertEquals(10, BigOExamples.first(new int[]{10, 20}).getIntValue());
    }

    @Test
    void linearSearchWorkGrowsWithPosition() {
        int[] numbers = {10, 20, 30, 40};
        assertEquals(1, BigOExamples.linearSearch(numbers, 10).getOperations());
        assertEquals(4, BigOExamples.linearSearch(numbers, 40).getOperations());
        assertEquals(-1, BigOExamples.linearSearch(numbers, 99).getIntValue());
    }

    @Test
    void binarySearchDiscardsHalfAtEachStep() {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8};
        assertEquals(4, BigOExamples.binarySearch(numbers, 8).getOperations());
        assertEquals(7, BigOExamples.binarySearch(numbers, 8).getIntValue());
    }

    @Test
    void mergeSortOrdersValuesWithoutChangingInput() {
        int[] input = {5, 1, 4, 2, 3};
        BigOExamples.Result result = BigOExamples.mergeSort(input);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.getArrayValue());
        assertArrayEquals(new int[]{5, 1, 4, 2, 3}, input);
    }

    @Test
    void quadraticExampleComparesEveryPair() {
        BigOExamples.Result result = BigOExamples.countEqualPairs(new int[]{1, 2, 1, 2});
        assertEquals(2, result.getIntValue());
        assertEquals(6, result.getOperations());
    }

    @Test
    void validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> BigOExamples.first(null));
        assertThrows(IllegalArgumentException.class, () -> BigOExamples.first(new int[0]));
        assertThrows(IllegalArgumentException.class, () -> BigOExamples.linearSearch(null, 1));
        assertThrows(IllegalArgumentException.class, () -> BigOExamples.binarySearch(null, 1));
        assertThrows(IllegalArgumentException.class, () -> BigOExamples.mergeSort(null));
        assertThrows(IllegalArgumentException.class, () -> BigOExamples.countEqualPairs(null));
    }
}
