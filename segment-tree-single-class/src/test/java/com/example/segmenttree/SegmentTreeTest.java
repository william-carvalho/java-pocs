package com.example.segmenttree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SegmentTreeTest {
    @Test
    void sumTreeQueriesRanges() {
        SegmentTree tree = SegmentTree.sum(new int[]{1, 3, 5, 7, 9, 11});

        assertEquals(36, tree.query(0, 5));
        assertEquals(15, tree.query(1, 3));
        assertEquals(9, tree.query(4, 4));
    }

    @Test
    void pointUpdateRefreshesAffectedRanges() {
        SegmentTree tree = SegmentTree.sum(new int[]{1, 3, 5, 7, 9, 11});

        tree.update(1, 10);

        assertEquals(43, tree.query(0, 5));
        assertEquals(22, tree.query(1, 3));
        assertEquals(10, tree.query(1, 1));
    }

    @Test
    void minTreeQueriesMinimum() {
        SegmentTree tree = SegmentTree.min(new int[]{8, 6, 7, 3, 9, 2});

        assertEquals(2, tree.query(0, 5));
        assertEquals(3, tree.query(2, 4));

        tree.update(5, 10);

        assertEquals(3, tree.query(0, 5));
    }

    @Test
    void maxTreeQueriesMaximum() {
        SegmentTree tree = SegmentTree.max(new int[]{8, 6, 7, 3, 9, 2});

        assertEquals(9, tree.query(0, 5));
        assertEquals(7, tree.query(1, 3));

        tree.update(3, 20);

        assertEquals(20, tree.query(0, 5));
    }

    @Test
    void gcdTreeQueriesGreatestCommonDivisor() {
        SegmentTree tree = SegmentTree.gcd(new int[]{12, 18, 24, 30});

        assertEquals(6, tree.query(0, 3));
        assertEquals(6, tree.query(1, 2));

        tree.update(2, 27);

        assertEquals(3, tree.query(0, 3));
    }

    @Test
    void supportsCustomOperation() {
        SegmentTree productTree = new SegmentTree(new int[]{2, 3, 4}, new SegmentTree.IntOperation() {
            public int apply(int left, int right) {
                return left * right;
            }
        }, 1);

        assertEquals(24, productTree.query(0, 2));
        assertEquals(12, productTree.query(1, 2));
    }

    @Test
    void worksWithSingleElementTree() {
        SegmentTree tree = SegmentTree.sum(new int[]{42});

        assertEquals(1, tree.size());
        assertEquals(42, tree.query(0, 0));

        tree.update(0, 7);

        assertEquals(7, tree.query(0, 0));
    }

    @Test
    void rejectsInvalidConstruction() {
        assertThrows(IllegalArgumentException.class, () -> SegmentTree.sum(null));
        assertThrows(IllegalArgumentException.class, () -> SegmentTree.sum(new int[0]));
        assertThrows(IllegalArgumentException.class, () -> new SegmentTree(new int[]{1}, null, 0));
    }

    @Test
    void rejectsInvalidQueriesAndUpdates() {
        SegmentTree tree = SegmentTree.sum(new int[]{1, 2, 3});

        assertThrows(IndexOutOfBoundsException.class, () -> tree.query(-1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> tree.query(2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> tree.query(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> tree.update(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> tree.update(3, 10));
    }

    @Test
    void treeSnapshotIsDefensiveCopy() {
        SegmentTree tree = SegmentTree.sum(new int[]{1, 2, 3});

        int[] first = tree.snapshotTreeArray();
        int[] second = tree.snapshotTreeArray();
        first[1] = 999;

        assertNotSame(first, second);
        assertEquals(6, second[1]);
        assertEquals(6, tree.query(0, 2));
    }
}
