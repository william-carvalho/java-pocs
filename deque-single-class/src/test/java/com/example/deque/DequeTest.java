package com.example.deque;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DequeTest {
    @Test
    void addsValuesAtBothEnds() {
        Deque<String> deque = new Deque<String>();

        deque.addFirst("b");
        deque.addFirst("a");
        deque.addLast("c");

        assertArrayEquals(new Object[]{"a", "b", "c"}, deque.toArray());
        assertEquals(3, deque.size());
    }

    @Test
    void removesValuesFromBothEnds() {
        Deque<Integer> deque = new Deque<Integer>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertEquals(Integer.valueOf(1), deque.removeFirst());
        assertEquals(Integer.valueOf(3), deque.removeLast());
        assertEquals(Integer.valueOf(2), deque.removeFirst());
        assertTrue(deque.isEmpty());
    }

    @Test
    void peeksWithoutRemoving() {
        Deque<String> deque = new Deque<String>();
        deque.addLast("first");
        deque.addLast("last");

        assertEquals("first", deque.peekFirst());
        assertEquals("last", deque.peekLast());
        assertEquals(2, deque.size());
    }

    @Test
    void removeFirstOccurrenceRemovesNearestHeadValue() {
        Deque<String> deque = new Deque<String>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("b");
        deque.addLast("c");

        assertTrue(deque.removeFirstOccurrence("b"));

        assertArrayEquals(new Object[]{"a", "b", "c"}, deque.toArray());
        assertFalse(deque.removeFirstOccurrence("x"));
    }

    @Test
    void removeLastOccurrenceRemovesNearestTailValue() {
        Deque<String> deque = new Deque<String>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("b");
        deque.addLast("c");

        assertTrue(deque.removeLastOccurrence("b"));

        assertArrayEquals(new Object[]{"a", "b", "c"}, deque.toArray());
        assertFalse(deque.removeLastOccurrence("x"));
    }

    @Test
    void supportsNullValues() {
        Deque<String> deque = new Deque<String>();
        deque.addLast("a");
        deque.addLast(null);
        deque.addLast("b");

        assertTrue(deque.contains(null));
        assertTrue(deque.removeFirstOccurrence(null));
        assertArrayEquals(new Object[]{"a", "b"}, deque.toArray());
    }

    @Test
    void iteratesForwardAndBackward() {
        Deque<Integer> deque = new Deque<Integer>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        Iterator<Integer> forward = deque.iterator();
        assertEquals(Integer.valueOf(1), forward.next());
        assertEquals(Integer.valueOf(2), forward.next());
        assertEquals(Integer.valueOf(3), forward.next());
        assertFalse(forward.hasNext());

        Iterator<Integer> backward = deque.descendingIterator();
        assertEquals(Integer.valueOf(3), backward.next());
        assertEquals(Integer.valueOf(2), backward.next());
        assertEquals(Integer.valueOf(1), backward.next());
        assertFalse(backward.hasNext());
    }

    @Test
    void clearResetsDequeForReuse() {
        Deque<Integer> deque = new Deque<Integer>();
        deque.addLast(1);
        deque.addLast(2);

        deque.clear();

        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertArrayEquals(new Object[0], deque.toArray());

        deque.addFirst(3);
        deque.addLast(4);
        assertArrayEquals(new Object[]{3, 4}, deque.toArray());
    }

    @Test
    void rejectsEmptyReadsAndRemovals() {
        Deque<Integer> deque = new Deque<Integer>();

        assertThrows(NoSuchElementException.class, deque::removeFirst);
        assertThrows(NoSuchElementException.class, deque::removeLast);
        assertThrows(NoSuchElementException.class, deque::peekFirst);
        assertThrows(NoSuchElementException.class, deque::peekLast);
    }

    @Test
    void iteratorsRejectInvalidOperations() {
        Deque<Integer> deque = new Deque<Integer>();
        deque.addLast(1);

        Iterator<Integer> forward = deque.iterator();
        assertEquals(Integer.valueOf(1), forward.next());
        assertThrows(NoSuchElementException.class, forward::next);
        assertThrows(UnsupportedOperationException.class, forward::remove);

        Iterator<Integer> backward = deque.descendingIterator();
        assertEquals(Integer.valueOf(1), backward.next());
        assertThrows(NoSuchElementException.class, backward::next);
        assertThrows(UnsupportedOperationException.class, backward::remove);
    }
}
