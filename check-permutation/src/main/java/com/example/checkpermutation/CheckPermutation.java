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
}
