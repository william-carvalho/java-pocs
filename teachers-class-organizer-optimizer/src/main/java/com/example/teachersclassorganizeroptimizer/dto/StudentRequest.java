package com.example.teachersclassorganizeroptimizer.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class StudentRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "schoolClassId is required")
    private Long schoolClassId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSchoolClassId() {
        return schoolClassId;
    }

    public void setSchoolClassId(Long schoolClassId) {
        this.schoolClassId = schoolClassId;
    }
}

