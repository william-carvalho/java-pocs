package com.example.customstring;

import com.example.customstring.model.CustomString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomStringTest {

    @Test
    void shouldReturnLength() {
        assertEquals(5, new CustomString("hello").length());
    }

    @Test
    void shouldReturnCharAt() {
        assertEquals('e', new CustomString("hello").charAt(1));
    }

    @Test
    void shouldThrowForInvalidCharAt() {
        assertThrows(IndexOutOfBoundsException.class, () -> new CustomString("hello").charAt(5));
    }

    @Test
    void shouldReverseString() {
        assertEquals("olleh", new CustomString("hello").reverse().toString());
    }

    @Test
    void shouldIterateCharacters() {
        List<Character> characters = new ArrayList<Character>();
        for (Character character : new CustomString("abc")) {
            characters.add(character);
        }
        assertEquals(3, characters.size());
        assertEquals(Character.valueOf('a'), characters.get(0));
        assertEquals(Character.valueOf('b'), characters.get(1));
        assertEquals(Character.valueOf('c'), characters.get(2));
    }

    @Test
    void shouldSupportForEach() {
        List<Character> characters = new ArrayList<Character>();
        new CustomString("abc").forEachChar(characters::add);

        assertEquals(3, characters.size());
        assertEquals(Character.valueOf('a'), characters.get(0));
        assertEquals(Character.valueOf('c'), characters.get(2));
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        CustomString left = new CustomString("abc");
        CustomString right = new CustomString("abc");

        assertTrue(left.equals(right));
        assertEquals(left.hashCode(), right.hashCode());
        assertFalse(left.equals(null));
        assertFalse(left.equals("abc"));
    }

    @Test
    void shouldCheckIfEmpty() {
        assertTrue(new CustomString("").isEmpty());
        assertFalse(new CustomString("a").isEmpty());
    }

    @Test
    void shouldReplaceCharacters() {
        assertEquals("bonono", new CustomString("banana").replace('a', 'o').toString());
        assertEquals("banana", new CustomString("banana").replace('x', 'y').toString());
    }

    @Test
    void shouldCreateSubstring() {
        CustomString text = new CustomString("abcdef");

        assertEquals("cdef", text.substring(2).toString());
        assertEquals("cde", text.substring(2, 5).toString());
    }

    @Test
    void shouldThrowForInvalidSubstring() {
        assertThrows(IndexOutOfBoundsException.class, () -> new CustomString("abc").substring(-1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> new CustomString("abc").substring(2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> new CustomString("abc").substring(0, 4));
    }

    @Test
    void shouldTrimWhitespace() {
        assertEquals("hello", new CustomString("  hello  ").trim().toString());
        assertEquals("hello", new CustomString("hello").trim().toString());
        assertEquals("", new CustomString("   ").trim().toString());
    }

    @Test
    void shouldConvertToJson() {
        assertEquals("\"Hello \\\"World\\\"\\n\"", new CustomString("Hello \"World\"\n").toJson());
    }

    @Test
    void shouldFindIndexOfCharacter() {
        assertEquals(2, new CustomString("banana").indexOf('n'));
        assertEquals(-1, new CustomString("banana").indexOf('x'));
    }

    @Test
    void shouldReturnDefensiveCopyFromToArray() {
        CustomString value = new CustomString("abc");
        char[] chars = value.toArray();
        chars[0] = 'z';

        assertArrayEquals(new char[]{'a', 'b', 'c'}, value.toArray());
    }
}

