package com.example.stack;

/** Solves the balanced-brackets problem with a stack. */
public final class BracketValidator {
    private BracketValidator() {
    }

    public static boolean isBalanced(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        SimpleStack<Character> openings = new SimpleStack<Character>();

        for (char character : text.toCharArray()) {
            if (character == '(' || character == '[' || character == '{') {
                openings.push(character);
            } else if (character == ')' || character == ']' || character == '}') {
                if (openings.isEmpty() || !matches(openings.pop(), character)) {
                    return false;
                }
            }
        }

        return openings.isEmpty();
    }

    private static boolean matches(char opening, char closing) {
        return opening == '(' && closing == ')'
                || opening == '[' && closing == ']'
                || opening == '{' && closing == '}';
    }
}
