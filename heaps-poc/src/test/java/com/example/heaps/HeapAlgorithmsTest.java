package com.example.heaps;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeapAlgorithmsTest {
    @Test
    void shouldSortWithoutChangingInput() {
        java.util.List<Integer> input = Arrays.asList(5, 1, 5, -2, 9);

        assertEquals(Arrays.asList(-2, 1, 5, 5, 9), HeapAlgorithms.heapSort(input));
        assertEquals(Arrays.asList(5, 1, 5, -2, 9), input);
    }

    @Test
    void shouldFindTopKValues() {
        java.util.List<Integer> values = Arrays.asList(4, 10, 3, 8, 10, 1);

        assertEquals(Arrays.asList(10, 10, 8), HeapAlgorithms.topK(values, 3));
        assertEquals(Collections.emptyList(), HeapAlgorithms.topK(values, 0));
        assertEquals(Arrays.asList(10, 10, 8, 4, 3, 1), HeapAlgorithms.topK(values, 20));
        assertThrows(IllegalArgumentException.class, () -> HeapAlgorithms.topK(values, -1));
    }

    @Test
    void shouldCalculateRunningMedianAndAvoidIntegerOverflow() {
        RunningMedian median = new RunningMedian();
        median.add(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, median.median());
        median.add(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, median.median());
        median.add(1);
        assertEquals(Integer.MAX_VALUE, median.median());
        median.add(2);
        assertEquals(1073741824.5, median.median());
    }
}
