package com.example.heaps;

import java.util.Arrays;
import java.util.List;

public final class HeapsPocApplication {
    private HeapsPocApplication() {
    }

    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(9, 4, 7, 1, 8, 2, 6);

        BinaryHeap<Integer> minHeap = new BinaryHeap<Integer>(
                values, java.util.Comparator.naturalOrder());
        BinaryHeap<Integer> maxHeap = new BinaryHeap<Integer>(
                values, java.util.Comparator.reverseOrder());

        System.out.println("Valores: " + values);
        System.out.println("Min-heap em nivel: " + minHeap.toLevelOrderList());
        System.out.println("Max-heap em nivel: " + maxHeap.toLevelOrderList());
        System.out.println("Heap sort: " + HeapAlgorithms.heapSort(values));
        System.out.println("Top 3: " + HeapAlgorithms.topK(values, 3));

        RunningMedian runningMedian = new RunningMedian();
        for (Integer value : values) {
            runningMedian.add(value);
            System.out.println("Mediana apos " + value + ": " + runningMedian.median());
        }
    }
}
