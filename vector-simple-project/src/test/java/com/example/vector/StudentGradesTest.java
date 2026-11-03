package com.example.vector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentGradesTest {
    @Test
    void calculatesTheGradeAverage() {
        StudentGrades grades = new StudentGrades();
        grades.add(8.0);
        grades.add(9.0);
        grades.add(7.0);

        assertEquals(8.0, grades.average(), 0.001);
        assertEquals(3, grades.count());
    }

    @Test
    void rejectsInvalidGrades() {
        StudentGrades grades = new StudentGrades();

        assertThrows(IllegalArgumentException.class, () -> grades.add(11));
    }

    @Test
    void emptyGradesHaveNoAverage() {
        StudentGrades grades = new StudentGrades();

        assertThrows(IllegalStateException.class, grades::average);
    }
}
