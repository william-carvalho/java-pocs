package com.example.converterframework.model.entity;

public class AddressEntity {

    private final String street;
    private final String city;
    private final String zipCode;

    public AddressEntity(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }
}

