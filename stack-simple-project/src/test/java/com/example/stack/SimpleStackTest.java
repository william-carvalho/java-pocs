package com.example.stack;

import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleStackTest {
    @Test
    void removesValuesInLastInFirstOutOrder() {
        SimpleStack<String> stack = new SimpleStack<String>();
        stack.push("first");
        stack.push("second");

        assertEquals("second", stack.pop());
        assertEquals("first", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekDoesNotRemoveTheValue() {
        SimpleStack<Integer> stack = new SimpleStack<Integer>();
        stack.push(10);

        assertEquals(Integer.valueOf(10), stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void emptyStackCannotBePopped() {
        SimpleStack<Integer> stack = new SimpleStack<Integer>();

        assertThrows(EmptyStackException.class, stack::pop);
    }
}
