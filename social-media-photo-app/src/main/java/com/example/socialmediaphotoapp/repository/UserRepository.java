package com.example.socialmediaphotoapp.repository;

import com.example.socialmediaphotoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

