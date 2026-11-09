package com.example.checkpermutation;

public class CheckPermutation {

   public String sort(String value) {
      char[] content = value.toCharArray();
      java.util.Arrays.sort(content);
      return new String(content);
   }

   public boolean checkPermutation(String value1, String value2) {
      if (value1.length() != value2.length()) {
         return false;
      }
      return sort(value1).equals(sort(value2));
   }

   public boolean permutation(String value1, String value2) {
      if (value1.length() != value2.length()) {
         return false;
      }

      int[] letters = new int[128]; // Assumption: ASCII
      char[] value1Array = value1.toCharArray();
      for (char c : value1Array) {
         letters[c]++;
      }

      for (int i = 0; i < value2.length(); i++) {
         int c = (int) value2.charAt(i);
         letters[c]--;
         if (letters[c] < 0) {
            return false;
         }
      }
      return true;
   }
}
