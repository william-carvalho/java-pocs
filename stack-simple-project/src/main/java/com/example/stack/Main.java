package com.example.stack;

public class Main {
    public static void main(String[] args) {
        SimpleStack<String> books = new SimpleStack<String>();
        books.push("Clean Code");
        books.push("Effective Java");

        System.out.println("Top book: " + books.peek());
        System.out.println("Removed: " + books.pop());
        System.out.println("Stack size: " + books.size());

        String expression = "{[2 + (3 * 4)]}";
        System.out.println("Balanced expression: "
                + BracketValidator.isBalanced(expression));
    }
}
