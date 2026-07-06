package com.example.reversestring;

public final class ReverseString {
    private ReverseString() {
    }

    public static String reverse(String value) {
        validate(value);
        char[] characters = value.toCharArray();
        int left = 0;
        int right = characters.length - 1;
        while (left < right) {
            char temporary = characters[left];
            characters[left] = characters[right];
            characters[right] = temporary;
            left++;
            right--;
        }
        return new String(characters);
    }

    public static String reverseWords(String value) {
        validate(value);
        if (value.isEmpty()) {
            return "";
        }

        String[] words = value.split(" ", -1);
        int left = 0;
        int right = words.length - 1;
        while (left < right) {
            String temporary = words[left];
            words[left] = words[right];
            words[right] = temporary;
            left++;
            right--;
        }
        return join(words, " ");
    }

    public static String reverseEachWord(String value) {
        validate(value);
        if (value.isEmpty()) {
            return "";
        }

        String[] words = value.split(" ", -1);
        for (int index = 0; index < words.length; index++) {
            words[index] = reverse(words[index]);
        }
        return join(words, " ");
    }

    public static boolean isPalindrome(String value) {
        validate(value);
        int left = 0;
        int right = value.length() - 1;
        while (left < right) {
            if (value.charAt(left) != value.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    private static String join(String[] values, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(separator);
            }
            builder.append(values[index]);
        }
        return builder.toString();
    }

    private static void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
    }
}
