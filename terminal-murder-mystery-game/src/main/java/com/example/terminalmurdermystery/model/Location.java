package com.example.terminalmurdermystery.model;

public class Location {

    private final String name;
    private final String description;

    public Location(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

