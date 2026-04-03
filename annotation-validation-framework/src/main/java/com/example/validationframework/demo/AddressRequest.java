package com.example.validationframework.demo;

import com.example.validationframework.annotation.NotBlank;
import com.example.validationframework.annotation.Pattern;

public class AddressRequest {

    @NotBlank(message = "Street is required")
    private final String street;

    @NotBlank(message = "City is required")
    private final String city;

    @Pattern(regex = "\\d{5}-\\d{3}", message = "Zip code must follow 99999-999")
    private final String zipCode;

    public AddressRequest(String street, String city, String zipCode) {
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

