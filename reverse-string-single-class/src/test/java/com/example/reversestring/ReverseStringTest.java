package com.example.reversestring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReverseStringTest {
    @Test
    void reversesSimpleText() {
        assertEquals("olleh", ReverseString.reverse("hello"));
        assertEquals("avaJ", ReverseString.reverse("Java"));
    }

    @Test
    void reversesEmptyAndSingleCharacterText() {
        assertEquals("", ReverseString.reverse(""));
        assertEquals("a", ReverseString.reverse("a"));
    }

    @Test
    void reversesTextWithSpacesAndPunctuation() {
        assertEquals("!dlrow ,olleH", ReverseString.reverse("Hello, world!"));
        assertEquals("  cba", ReverseString.reverse("abc  "));
    }

    @Test
    void reversesWordsOrder() {
        assertEquals("three two one", ReverseString.reverseWords("one two three"));
    }

    @Test
    void reverseWordsPreservesRepeatedSpaces() {
        assertEquals("three  two one", ReverseString.reverseWords("one two  three"));
        assertEquals(" world hello ", ReverseString.reverseWords(" hello world "));
    }

    @Test
    void reversesEachWordIndividually() {
        assertEquals("olleh dlrow", ReverseString.reverseEachWord("hello world"));
        assertEquals("a  cb", ReverseString.reverseEachWord("a  bc"));
    }

    @Test
    void detectsPalindromes() {
        assertTrue(ReverseString.isPalindrome(""));
        assertTrue(ReverseString.isPalindrome("a"));
        assertTrue(ReverseString.isPalindrome("level"));
        assertTrue(ReverseString.isPalindrome("abba"));
    }

    @Test
    void detectsNonPalindromes() {
        assertFalse(ReverseString.isPalindrome("hello"));
        assertFalse(ReverseString.isPalindrome("Level"));
    }

    @Test
    void handlesNumbersAsText() {
        assertEquals("54321", ReverseString.reverse("12345"));
        assertTrue(ReverseString.isPalindrome("12321"));
    }

    @Test
    void rejectsNullInput() {
        assertThrows(IllegalArgumentException.class, () -> ReverseString.reverse(null));
        assertThrows(IllegalArgumentException.class, () -> ReverseString.reverseWords(null));
        assertThrows(IllegalArgumentException.class, () -> ReverseString.reverseEachWord(null));
        assertThrows(IllegalArgumentException.class, () -> ReverseString.isPalindrome(null));
    }
}
