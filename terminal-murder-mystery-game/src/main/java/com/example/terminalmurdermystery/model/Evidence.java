package com.example.terminalmurdermystery.model;

public class Evidence {

    private final String fileName;
    private final String description;
    private final String clueText;

    public Evidence(String fileName, String description, String clueText) {
        this.fileName = fileName;
        this.description = description;
        this.clueText = clueText;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public String getClueText() {
        return clueText;
    }
}

