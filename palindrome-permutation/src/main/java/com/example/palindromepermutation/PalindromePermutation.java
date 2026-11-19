package com.example.palindromepermutation;

public class PalindromePermutation {

 public boolean isPermutationOfPalindrome(String phrase) {
  int[] table = buildCharFrequencyTable(phrase);
  return checkMaxOneOdd(table);
 }
 public int[] buildCharFrequencyTable(String phrase) {
  int[] table = new int[Character.getNumericValue('z') - Character.getNumericValue('a') + 1];
  for (char c : phrase.toCharArray()) {
   int x = getCharNumber(c);
   if (x != -1) {
    table[x]++;
   }
  }
  return table;
 }
 
 
 
 public int getCharNumber(Character c) {
  int a = Character.getNumericValue('a');
  int z = Character.getNumericValue('z');
  int val = Character.getNumericValue(c);
    if (a <= val && val <= z) {
        return val - a;
    }       
    return -1;
 }
 
}
