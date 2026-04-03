package com.example.teachersclassorganizeroptimizer.dto;

import javax.validation.constraints.NotBlank;

public class SchoolClassRequest {

    @NotBlank(message = "name is required")
    private String name;
    private String gradeLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}

