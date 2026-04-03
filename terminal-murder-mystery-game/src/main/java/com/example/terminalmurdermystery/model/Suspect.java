package com.example.terminalmurdermystery.model;

public class Suspect {

    private final String name;
    private final String role;
    private final String alibi;
    private final String notes;

    public Suspect(String name, String role, String alibi, String notes) {
        this.name = name;
        this.role = role;
        this.alibi = alibi;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getAlibi() {
        return alibi;
    }

    public String getNotes() {
        return notes;
    }
}

