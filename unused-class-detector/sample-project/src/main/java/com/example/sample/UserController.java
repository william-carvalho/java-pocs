package com.example.sample;

public class UserController {

    private final UserService userService = new UserService();

    public String handle() {
        return userService.getUser();
    }
}

