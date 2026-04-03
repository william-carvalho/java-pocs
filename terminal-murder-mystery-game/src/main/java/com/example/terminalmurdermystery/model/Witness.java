package com.example.terminalmurdermystery.model;

public class Witness {

    private final String name;
    private final String statement;

    public Witness(String name, String statement) {
        this.name = name;
        this.statement = statement;
    }

    public String getName() {
        return name;
    }

    public String getStatement() {
        return statement;
    }
}

