package com.example.vector;

/** Practical use of a vector to store a variable number of grades. */
public class StudentGrades {
    private final SimpleVector<Double> grades = new SimpleVector<Double>();

    public void add(double grade) {
        if (grade < 0 || grade > 10) {
            throw new IllegalArgumentException("Grade must be between 0 and 10");
        }
        grades.add(grade);
    }

    public double average() {
        if (grades.isEmpty()) {
            throw new IllegalStateException("There are no grades");
        }

        double total = 0;
        for (int index = 0; index < grades.size(); index++) {
            total += grades.get(index);
        }
        return total / grades.size();
    }

    public int count() {
        return grades.size();
    }
}
