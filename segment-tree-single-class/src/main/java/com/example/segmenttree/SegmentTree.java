package com.example.segmenttree;

public final class SegmentTree {
    private final int size;
    private final int[] tree;
    private final IntOperation operation;
    private final int identity;

    public SegmentTree(int[] values, IntOperation operation, int identity) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (operation == null) {
            throw new IllegalArgumentException("operation is required");
        }
        this.size = values.length;
        this.operation = operation;
        this.identity = identity;
        this.tree = new int[size * 4];
        build(values, 1, 0, size - 1);
    }

    public static SegmentTree sum(int[] values) {
        return new SegmentTree(values, new IntOperation() {
            public int apply(int left, int right) {
                return left + right;
            }
        }, 0);
    }

    public static SegmentTree min(int[] values) {
        return new SegmentTree(values, new IntOperation() {
            public int apply(int left, int right) {
                return Math.min(left, right);
            }
        }, Integer.MAX_VALUE);
    }

    public static SegmentTree max(int[] values) {
        return new SegmentTree(values, new IntOperation() {
            public int apply(int left, int right) {
                return Math.max(left, right);
            }
        }, Integer.MIN_VALUE);
    }

    public static SegmentTree gcd(int[] values) {
        return new SegmentTree(values, new IntOperation() {
            public int apply(int left, int right) {
                return gcdValue(left, right);
            }
        }, 0);
    }

    public int query(int left, int right) {
        validateRange(left, right);
        return query(1, 0, size - 1, left, right);
    }

    public void update(int index, int value) {
        validateIndex(index);
        update(1, 0, size - 1, index, value);
    }

    public int size() {
        return size;
    }

    public int[] snapshotTreeArray() {
        int[] copy = new int[tree.length];
        for (int index = 0; index < tree.length; index++) {
            copy[index] = tree[index];
        }
        return copy;
    }

    private void build(int[] values, int node, int start, int end) {
        if (start == end) {
            tree[node] = values[start];
            return;
        }
        int mid = start + (end - start) / 2;
        build(values, node * 2, start, mid);
        build(values, node * 2 + 1, mid + 1, end);
        tree[node] = operation.apply(tree[node * 2], tree[node * 2 + 1]);
    }

    private int query(int node, int start, int end, int left, int right) {
        if (right < start || end < left) {
            return identity;
        }
        if (left <= start && end <= right) {
            return tree[node];
        }
        int mid = start + (end - start) / 2;
        int leftResult = query(node * 2, start, mid, left, right);
        int rightResult = query(node * 2 + 1, mid + 1, end, left, right);
        return operation.apply(leftResult, rightResult);
    }

    private void update(int node, int start, int end, int index, int value) {
        if (start == end) {
            tree[node] = value;
            return;
        }
        int mid = start + (end - start) / 2;
        if (index <= mid) {
            update(node * 2, start, mid, index, value);
        } else {
            update(node * 2 + 1, mid + 1, end, index, value);
        }
        tree[node] = operation.apply(tree[node * 2], tree[node * 2 + 1]);
    }

    private void validateRange(int left, int right) {
        if (left < 0 || right >= size || left > right) {
            throw new IndexOutOfBoundsException("invalid range: [" + left + ", " + right + "]");
        }
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index);
        }
    }

    private static int gcdValue(int left, int right) {
        int a = Math.abs(left);
        int b = Math.abs(right);
        while (b != 0) {
            int next = a % b;
            a = b;
            b = next;
        }
        return a;
    }

    public interface IntOperation {
        int apply(int left, int right);
    }
}
