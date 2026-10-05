package com.example.bigo;

import java.util.Arrays;

/**
 * Exemplos pequenos de Big O. Cada metodo devolve tambem quantas operacoes
 * importantes foram feitas, para que o crescimento fique visivel.
 */
public final class BigOExamples {
    private BigOExamples() {
    }

    /** O(1): acessar uma posicao leva uma operacao, qualquer que seja o tamanho. */
    public static Result first(int[] numbers) {
        validateNotEmpty(numbers);
        return new Result(numbers[0], 1);
    }

    /** O(n): no pior caso, olha todos os elementos uma vez. */
    public static Result linearSearch(int[] numbers, int target) {
        validate(numbers);
        for (int index = 0; index < numbers.length; index++) {
            if (numbers[index] == target) {
                return new Result(index, index + 1);
            }
        }
        return new Result(-1, numbers.length);
    }

    /** O(log n): a cada comparacao elimina aproximadamente metade da busca. */
    public static Result binarySearch(int[] sortedNumbers, int target) {
        validate(sortedNumbers);
        int left = 0;
        int right = sortedNumbers.length - 1;
        int operations = 0;

        while (left <= right) {
            operations++;
            int middle = left + (right - left) / 2;
            if (sortedNumbers[middle] == target) {
                return new Result(middle, operations);
            }
            if (sortedNumbers[middle] < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }
        return new Result(-1, operations);
    }

    /** O(n log n): merge sort divide a lista e depois junta as partes ordenadas. */
    public static Result mergeSort(int[] numbers) {
        validate(numbers);
        int[] copy = Arrays.copyOf(numbers, numbers.length);
        int operations = mergeSort(copy, new int[copy.length], 0, copy.length - 1);
        return new Result(copy, operations);
    }

    /** O(n²): compara cada par de elementos. */
    public static Result countEqualPairs(int[] numbers) {
        validate(numbers);
        int equalPairs = 0;
        int operations = 0;
        for (int first = 0; first < numbers.length; first++) {
            for (int second = first + 1; second < numbers.length; second++) {
                operations++;
                if (numbers[first] == numbers[second]) {
                    equalPairs++;
                }
            }
        }
        return new Result(equalPairs, operations);
    }

    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8};

        System.out.println("Big O compara como o trabalho cresce quando a entrada aumenta.\n");
        print("O(1)      - primeiro elemento", first(numbers));
        print("O(log n)  - busca binaria pelo 8", binarySearch(numbers, 8));
        print("O(n)      - busca linear pelo 8", linearSearch(numbers, 8));
        print("O(n log n)- merge sort", mergeSort(new int[]{8, 3, 6, 1, 7, 2, 5, 4}));
        print("O(n^2)    - comparar todos os pares", countEqualPairs(numbers));

        System.out.println("\nIdeia principal: Big O nao mede segundos; descreve a tendencia de crescimento.");
    }

    private static int mergeSort(int[] values, int[] helper, int left, int right) {
        if (left >= right) {
            return 0;
        }
        int middle = left + (right - left) / 2;
        int operations = mergeSort(values, helper, left, middle)
                + mergeSort(values, helper, middle + 1, right);
        int first = left;
        int second = middle + 1;
        int destination = left;

        while (first <= middle && second <= right) {
            operations++;
            helper[destination++] = values[first] <= values[second]
                    ? values[first++] : values[second++];
        }
        while (first <= middle) {
            helper[destination++] = values[first++];
        }
        while (second <= right) {
            helper[destination++] = values[second++];
        }
        for (int index = left; index <= right; index++) {
            values[index] = helper[index];
        }
        return operations;
    }

    private static void print(String label, Result result) {
        System.out.printf("%-39s resultado=%-25s operacoes=%d%n",
                label, result.getValue(), result.getOperations());
    }

    private static void validate(int[] numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("numbers is required");
        }
    }

    private static void validateNotEmpty(int[] numbers) {
        validate(numbers);
        if (numbers.length == 0) {
            throw new IllegalArgumentException("numbers must not be empty");
        }
    }

    public static final class Result {
        private final Object value;
        private final int operations;

        private Result(Object value, int operations) {
            this.value = value;
            this.operations = operations;
        }

        public Object getValue() {
            return value instanceof int[] ? Arrays.toString((int[]) value) : value;
        }

        public int getIntValue() {
            return (Integer) value;
        }

        public int[] getArrayValue() {
            return Arrays.copyOf((int[]) value, ((int[]) value).length);
        }

        public int getOperations() {
            return operations;
        }
    }
}
