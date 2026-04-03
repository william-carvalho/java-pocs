package com.example.sample;

public class UserRepository {

    public String findById(Long id) {
        return "user-" + id;
    }
}

