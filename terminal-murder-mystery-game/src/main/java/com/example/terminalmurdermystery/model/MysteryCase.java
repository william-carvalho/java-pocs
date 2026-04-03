package com.example.terminalmurdermystery.model;

import java.util.List;
import java.util.Map;

public class MysteryCase {

    private final String title;
    private final String summary;
    private final List<Suspect> suspects;
    private final List<Witness> witnesses;
    private final List<Location> locations;
    private final List<Evidence> evidence;
    private final Map<String, String> logs;
    private final Map<String, String> reports;
    private final List<String> hints;
    private final CaseSolution solution;

    public MysteryCase(String title,
                       String summary,
                       List<Suspect> suspects,
                       List<Witness> witnesses,
                       List<Location> locations,
                       List<Evidence> evidence,
                       Map<String, String> logs,
                       Map<String, String> reports,
                       List<String> hints,
                       CaseSolution solution) {
        this.title = title;
        this.summary = summary;
        this.suspects = suspects;
        this.witnesses = witnesses;
        this.locations = locations;
        this.evidence = evidence;
        this.logs = logs;
        this.reports = reports;
        this.hints = hints;
        this.solution = solution;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public List<Suspect> getSuspects() {
        return suspects;
    }

    public List<Witness> getWitnesses() {
        return witnesses;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Evidence> getEvidence() {
        return evidence;
    }

    public Map<String, String> getLogs() {
        return logs;
    }

    public Map<String, String> getReports() {
        return reports;
    }

    public List<String> getHints() {
        return hints;
    }

    public CaseSolution getSolution() {
        return solution;
    }
}

