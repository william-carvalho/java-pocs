package com.example.customstring;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomStringTest {
    @Test
    void toArrayReturnsDefensiveCopy() {
        CustomString value = new CustomString("abc");

        char[] chars = value.toArray();
        chars[0] = 'x';

        assertArrayEquals(new char[]{'a', 'b', 'c'}, value.toArray());
    }

    @Test
    void foreachVisitsEveryCharacterInOrder() {
        CustomString value = new CustomString("abc");
        final StringBuilder visited = new StringBuilder();

        value.foreach(new CustomString.CharAction() {
            public void accept(char value) {
                visited.append(value).append('-');
            }
        });

        assertEquals("a-b-c-", visited.toString());
    }

    @Test
    void reverseReturnsNewCustomString() {
        CustomString value = new CustomString("hello");

        CustomString reversed = value.reverse();

        assertEquals("olleh", reversed.toString());
        assertEquals("hello", value.toString());
    }

    @Test
    void iteratorReadsCharactersAndRejectsRemove() {
        Iterator<Character> iterator = new CustomString("ab").iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Character.valueOf('a'), iterator.next());
        assertEquals(Character.valueOf('b'), iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void lengthAndCharAtWork() {
        CustomString value = new CustomString("hello");

        assertEquals(5, value.length());
        assertEquals('h', value.charAt(0));
        assertEquals('o', value.charAt(4));
    }

    @Test
    void charAtRejectsInvalidIndexes() {
        CustomString value = new CustomString("abc");

        assertThrows(IndexOutOfBoundsException.class, () -> value.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> value.charAt(3));
    }

    @Test
    void equalsAndHashCodeUseCharacterContent() {
        CustomString first = new CustomString("abc");
        CustomString second = new CustomString("abc");
        CustomString third = new CustomString("abd");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, "abc");
    }

    @Test
    void isEmptySupportsEmptyAndNullInput() {
        assertTrue(new CustomString("").isEmpty());
        assertTrue(new CustomString(null).isEmpty());
        assertFalse(new CustomString("x").isEmpty());
    }

    @Test
    void replaceChangesMatchingCharactersOnly() {
        CustomString value = new CustomString("banana");

        assertEquals("bonono", value.replace('a', 'o').toString());
        assertEquals("banana", value.toString());
    }

    @Test
    void substringSupportsBeginAndRange() {
        CustomString value = new CustomString("abcdef");

        assertEquals("cdef", value.substring(2).toString());
        assertEquals("bcd", value.substring(1, 4).toString());
        assertEquals("", value.substring(2, 2).toString());
    }

    @Test
    void substringRejectsInvalidRanges() {
        CustomString value = new CustomString("abc");

        assertThrows(IndexOutOfBoundsException.class, () -> value.substring(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> value.substring(2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> value.substring(0, 4));
    }

    @Test
    void trimRemovesLeadingAndTrailingWhitespace() {
        assertEquals("hello", new CustomString("  hello  ").trim().toString());
        assertEquals("hello", new CustomString("\n\thello\r\n").trim().toString());
        assertEquals("", new CustomString(" \t\n ").trim().toString());
    }

    @Test
    void toJsonEscapesSpecialCharacters() {
        CustomString value = new CustomString("a\"b\\c\n\t");

        assertEquals("\"a\\\"b\\\\c\\n\\t\"", value.toJson());
    }

    @Test
    void indexOfFindsFirstOccurrenceOrMinusOne() {
        CustomString value = new CustomString("banana");

        assertEquals(1, value.indexOf('a'));
        assertEquals(2, value.indexOf('n'));
        assertEquals(-1, value.indexOf('x'));
    }

    @Test
    void worksInEnhancedForLoop() {
        CustomString value = new CustomString("abc");
        StringBuilder builder = new StringBuilder();

        for (Character character : value) {
            builder.append(character);
        }

        assertEquals("abc", builder.toString());
    }
}
