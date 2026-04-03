package com.example.terminalmurdermystery.model;

public class CaseSolution {

    private final String killerName;
    private final String weapon;
    private final String location;

    public CaseSolution(String killerName, String weapon, String location) {
        this.killerName = killerName;
        this.weapon = weapon;
        this.location = location;
    }

    public String getKillerName() {
        return killerName;
    }

    public String getWeapon() {
        return weapon;
    }

    public String getLocation() {
        return location;
    }
}

