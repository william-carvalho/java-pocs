package com.example.stack;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StackTest {
    @Test
    void startsEmpty() {
        Stack<String> stack = new Stack<String>();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertArrayEquals(new Object[0], stack.toArray());
    }

    @Test
    void pushesValuesOnTop() {
        Stack<String> stack = new Stack<String>();

        stack.push("a");
        stack.push("b");
        stack.push("c");

        assertArrayEquals(new Object[]{"c", "b", "a"}, stack.toArray());
        assertEquals(3, stack.size());
        assertEquals("c", stack.peek());
    }

    @Test
    void popsValuesInLastInFirstOutOrder() {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(Integer.valueOf(3), stack.pop());
        assertEquals(Integer.valueOf(2), stack.pop());
        assertEquals(Integer.valueOf(1), stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekReturnsTopWithoutRemovingIt() {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(10);
        stack.push(20);

        assertEquals(Integer.valueOf(20), stack.peek());
        assertEquals(2, stack.size());
        assertEquals(Integer.valueOf(20), stack.pop());
    }

    @Test
    void searchesFromTop() {
        Stack<String> stack = new Stack<String>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");

        assertEquals(0, stack.search("top"));
        assertEquals(1, stack.search("middle"));
        assertEquals(2, stack.search("bottom"));
        assertEquals(-1, stack.search("missing"));
        assertTrue(stack.contains("middle"));
        assertFalse(stack.contains("missing"));
    }

    @Test
    void supportsNullValues() {
        Stack<String> stack = new Stack<String>();
        stack.push("a");
        stack.push(null);
        stack.push("b");

        assertTrue(stack.contains(null));
        assertEquals(1, stack.search(null));
        assertArrayEquals(new Object[]{"b", null, "a"}, stack.toArray());
        assertEquals("b", stack.pop());
        assertEquals(null, stack.pop());
    }

    @Test
    void clearResetsStackForReuse() {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(1);
        stack.push(2);

        stack.clear();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertArrayEquals(new Object[0], stack.toArray());

        stack.push(3);
        stack.push(4);
        assertArrayEquals(new Object[]{4, 3}, stack.toArray());
    }

    @Test
    void iteratorVisitsFromTopToBottom() {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(7);
        stack.push(8);

        Iterator<Integer> iterator = stack.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(8), iterator.next());
        assertEquals(Integer.valueOf(7), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void rejectsEmptyReadsAndRemovals() {
        Stack<Integer> stack = new Stack<Integer>();

        assertThrows(NoSuchElementException.class, stack::pop);
        assertThrows(NoSuchElementException.class, stack::peek);
    }

    @Test
    void iteratorRejectsInvalidOperations() {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(1);

        Iterator<Integer> iterator = stack.iterator();

        assertEquals(Integer.valueOf(1), iterator.next());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
