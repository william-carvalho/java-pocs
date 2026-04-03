package com.example.teachersclassorganizeroptimizer.dto;

import javax.validation.constraints.NotBlank;

public class SubjectRequest {

    @NotBlank(message = "name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

