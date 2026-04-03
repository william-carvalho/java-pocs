package com.example.teachersclassorganizeroptimizer.dto;

import javax.validation.constraints.NotBlank;

public class TeacherRequest {

    @NotBlank(message = "name is required")
    private String name;
    private String subjectSpecialty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectSpecialty() {
        return subjectSpecialty;
    }

    public void setSubjectSpecialty(String subjectSpecialty) {
        this.subjectSpecialty = subjectSpecialty;
    }
}

