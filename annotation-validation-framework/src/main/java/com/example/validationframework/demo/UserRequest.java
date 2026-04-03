package com.example.validationframework.demo;

import com.example.validationframework.annotation.Email;
import com.example.validationframework.annotation.Max;
import com.example.validationframework.annotation.Min;
import com.example.validationframework.annotation.NotBlank;
import com.example.validationframework.annotation.Size;

public class UserRequest {

    @NotBlank(message = "Name is required")
    private final String name;

    @Email(message = "Invalid email")
    private final String email;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private final String password;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be at most 100")
    private final Integer age;

    public UserRequest(String name, String email, String password, Integer age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }
}

