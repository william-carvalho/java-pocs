package com.example.stack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BracketValidatorTest {
    @Test
    void acceptsBalancedBrackets() {
        assertTrue(BracketValidator.isBalanced("{[2 + (3 * 4)]}"));
        assertTrue(BracketValidator.isBalanced("no brackets"));
    }

    @Test
    void rejectsUnbalancedBrackets() {
        assertFalse(BracketValidator.isBalanced("([)]"));
        assertFalse(BracketValidator.isBalanced("(()"));
        assertFalse(BracketValidator.isBalanced("]"));
    }

    @Test
    void rejectsNullText() {
        assertThrows(IllegalArgumentException.class,
                () -> BracketValidator.isBalanced(null));
    }
}
