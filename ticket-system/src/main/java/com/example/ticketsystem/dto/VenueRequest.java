package com.example.ticketsystem.dto;

import javax.validation.constraints.NotBlank;

public class VenueRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
