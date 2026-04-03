package com.example.converterframework.model.entity;

public class UserEntity {

    private final Long id;
    private final String name;
    private final String email;
    private final AddressEntity address;

    public UserEntity(Long id, String name, String email, AddressEntity address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public AddressEntity getAddress() {
        return address;
    }
}

