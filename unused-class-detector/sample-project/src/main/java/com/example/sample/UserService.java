package com.example.sample;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public String getUser() {
        return userRepository.findById(1L);
    }
}

