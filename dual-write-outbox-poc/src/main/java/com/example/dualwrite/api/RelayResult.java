package com.example.dualwrite.api;

public class RelayResult {

    private final int selected;
    private final int published;
    private final int deduplicated;
    private final int failed;

    public RelayResult(int selected, int published, int deduplicated, int failed) {
        this.selected = selected;
        this.published = published;
        this.deduplicated = deduplicated;
        this.failed = failed;
    }

    public int getSelected() {
        return selected;
    }

    public int getPublished() {
        return published;
    }

    public int getDeduplicated() {
        return deduplicated;
    }

    public int getFailed() {
        return failed;
    }
}
