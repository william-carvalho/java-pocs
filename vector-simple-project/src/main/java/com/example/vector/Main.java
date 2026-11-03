package com.example.vector;

public class Main {
    public static void main(String[] args) {
        SimpleVector<String> languages = new SimpleVector<String>();
        languages.add("Java");
        languages.add("Python");
        languages.add("Go");

        System.out.println("First language: " + languages.get(0));
        System.out.println("Vector size: " + languages.size());
        System.out.println("Contains Java: " + languages.contains("Java"));

        StudentGrades grades = new StudentGrades();
        grades.add(8.0);
        grades.add(9.0);
        grades.add(7.0);
        System.out.println("Grade average: " + grades.average());
    }
}
