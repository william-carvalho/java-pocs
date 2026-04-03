package com.example.teachersclassorganizeroptimizer.dto;

public class StudentResponse {

    private Long id;
    private String name;
    private SchoolClassResponse schoolClass;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SchoolClassResponse getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClassResponse schoolClass) {
        this.schoolClass = schoolClass;
    }
}

