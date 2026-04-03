package com.example.customstring.demo;

import com.example.customstring.model.CustomString;

public class CustomStringDemo {

    public static void main(String[] args) {
        CustomString original = new CustomString("hello");
        CustomString trimmed = new CustomString("  hello  ");
        CustomString json = new CustomString("Hello \"World\"\n");

        System.out.println("Original: " + original);
        System.out.println("Length: " + original.length());
        System.out.println("Char at 1: " + original.charAt(1));
        System.out.println("Reverse: " + original.reverse());
        System.out.println("Equals 'hello': " + original.equals(new CustomString("hello")));
        System.out.println("Is empty: " + original.isEmpty());
        System.out.println("Replace l->x: " + original.replace('l', 'x'));
        System.out.println("Substring(1,4): " + original.substring(1, 4));
        System.out.println("Trim: " + trimmed.trim());
        System.out.println("Index of 'l': " + original.indexOf('l'));
        System.out.println("To JSON: " + json.toJson());
        System.out.println("HashCode: " + original.hashCode());

        System.out.print("ForEach: ");
        original.forEachChar(ch -> System.out.print(ch + " "));
        System.out.println();

        System.out.print("Iterator: ");
        for (Character character : original) {
            System.out.print(character + " ");
        }
        System.out.println();

        try {
            original.charAt(99);
        } catch (IndexOutOfBoundsException exception) {
            System.out.println("Invalid charAt handled: " + exception.getMessage());
        }
    }
}

