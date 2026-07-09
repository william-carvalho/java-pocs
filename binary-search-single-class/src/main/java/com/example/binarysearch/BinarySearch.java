package com.example.binarysearch;

import java.util.List;

public final class BinarySearch {
    private BinarySearch() {
    }

    public static int search(int[] values, int target) {
        validate(values);
        int left = 0;
        int right = values.length - 1;
        while (left <= right) {
            int middle = left + (right - left) / 2;
            if (values[middle] == target) {
                return middle;
            }
            if (values[middle] < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }
        return -1;
    }

    public static int firstIndexOf(int[] values, int target) {
        validate(values);
        int index = -1;
        int left = 0;
        int right = values.length - 1;
        while (left <= right) {
            int middle = left + (right - left) / 2;
            if (values[middle] >= target) {
                if (values[middle] == target) {
                    index = middle;
                }
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        return index;
    }

    public static int lastIndexOf(int[] values, int target) {
        validate(values);
        int index = -1;
        int left = 0;
        int right = values.length - 1;
        while (left <= right) {
            int middle = left + (right - left) / 2;
            if (values[middle] <= target) {
                if (values[middle] == target) {
                    index = middle;
                }
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }
        return index;
    }

    public static int lowerBound(int[] values, int target) {
        validate(values);
        int left = 0;
        int right = values.length;
        while (left < right) {
            int middle = left + (right - left) / 2;
            if (values[middle] < target) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }
        return left;
    }

    public static int upperBound(int[] values, int target) {
        validate(values);
        int left = 0;
        int right = values.length;
        while (left < right) {
            int middle = left + (right - left) / 2;
            if (values[middle] <= target) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }
        return left;
    }

    public static <T extends Comparable<T>> int search(List<T> values, T target) {
        validate(values, target);
        int left = 0;
        int right = values.size() - 1;
        while (left <= right) {
            int middle = left + (right - left) / 2;
            int comparison = values.get(middle).compareTo(target);
            if (comparison == 0) {
                return middle;
            }
            if (comparison < 0) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }
        return -1;
    }

    private static void validate(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values is required");
        }
    }

    private static <T extends Comparable<T>> void validate(List<T> values, T target) {
        if (values == null) {
            throw new IllegalArgumentException("values is required");
        }
        if (target == null) {
            throw new IllegalArgumentException("target is required");
        }
        for (T value : values) {
            if (value == null) {
                throw new IllegalArgumentException("values must not contain null");
            }
        }
    }
}
