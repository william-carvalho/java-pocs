package com.example.stringuniquecharacters;

public class StringUniqueCharacters {

    public static boolean isUniqueChars(String value) {
        if (value.length() > 128) {
            return false;
        }
        boolean[] charSet = new boolean[128]; 
        
        for (int i = 0; i < value.length(); i++) {
            int val = value.charAt(i);
            if (charSet[val]) {
                return false;
            }
            charSet[val] = true;
        }
        return true;
    }
}
