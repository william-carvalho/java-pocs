package com.example.circularqueue;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircularQueueTest {
    @Test
    void startsEmptyWithConfiguredCapacity() {
        CircularQueue<String> queue = new CircularQueue<String>(3);

        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertEquals(0, queue.size());
        assertEquals(3, queue.capacity());
        assertEquals(3, queue.remainingCapacity());
        assertArrayEquals(new Object[0], queue.toArray());
    }

    @Test
    void enqueuesAndDequeuesInFirstInFirstOutOrder() {
        CircularQueue<String> queue = new CircularQueue<String>(3);

        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");

        assertTrue(queue.isFull());
        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertEquals("c", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekReturnsFrontWithoutRemovingIt() {
        CircularQueue<Integer> queue = new CircularQueue<Integer>(2);

        queue.enqueue(10);
        queue.enqueue(20);

        assertEquals(Integer.valueOf(10), queue.peek());
        assertEquals(2, queue.size());
        assertEquals(Integer.valueOf(10), queue.dequeue());
    }

    @Test
    void wrapsTailAfterDequeues() {
        CircularQueue<Integer> queue = new CircularQueue<Integer>(3);

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(Integer.valueOf(1), queue.dequeue());
        assertEquals(Integer.valueOf(2), queue.dequeue());

        queue.enqueue(4);
        queue.enqueue(5);

        assertArrayEquals(new Object[]{3, 4, 5}, queue.toArray());
        assertTrue(queue.isFull());
        assertEquals(Integer.valueOf(3), queue.dequeue());
        assertEquals(Integer.valueOf(4), queue.dequeue());
        assertEquals(Integer.valueOf(5), queue.dequeue());
    }

    @Test
    void supportsNullValues() {
        CircularQueue<String> queue = new CircularQueue<String>(2);

        queue.enqueue(null);
        queue.enqueue("b");

        assertArrayEquals(new Object[]{null, "b"}, queue.toArray());
        assertEquals(null, queue.peek());
        assertEquals(null, queue.dequeue());
        assertEquals("b", queue.dequeue());
    }

    @Test
    void clearResetsQueue() {
        CircularQueue<Integer> queue = new CircularQueue<Integer>(3);
        queue.enqueue(1);
        queue.enqueue(2);

        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertEquals(3, queue.remainingCapacity());
        queue.enqueue(3);
        assertArrayEquals(new Object[]{3}, queue.toArray());
    }

    @Test
    void rejectsOverflow() {
        CircularQueue<Integer> queue = new CircularQueue<Integer>(1);
        queue.enqueue(1);

        assertThrows(IllegalStateException.class, () -> queue.enqueue(2));
    }

    @Test
    void rejectsUnderflow() {
        CircularQueue<Integer> queue = new CircularQueue<Integer>(1);

        assertThrows(NoSuchElementException.class, queue::dequeue);
        assertThrows(NoSuchElementException.class, queue::peek);
    }

    @Test
    void rejectsInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<Integer>(-1));
    }

    @Test
    void emptyingQueueResetsIndexesForReuse() {
        CircularQueue<Integer> queue = new CircularQueue<Integer>(2);
        queue.enqueue(1);
        queue.enqueue(2);
        assertEquals(Integer.valueOf(1), queue.dequeue());
        assertEquals(Integer.valueOf(2), queue.dequeue());

        queue.enqueue(3);
        queue.enqueue(4);

        assertArrayEquals(new Object[]{3, 4}, queue.toArray());
        assertEquals(Integer.valueOf(3), queue.dequeue());
        assertEquals(Integer.valueOf(4), queue.dequeue());
    }
}
