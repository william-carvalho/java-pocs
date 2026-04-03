package com.example.socialmediaphotoapp.service;

import com.example.socialmediaphotoapp.dto.UserRequest;
import com.example.socialmediaphotoapp.dto.UserResponse;
import com.example.socialmediaphotoapp.entity.User;
import com.example.socialmediaphotoapp.exception.ResourceNotFoundException;
import com.example.socialmediaphotoapp.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        User user = new User();
        user.setName(request.getName().trim());
        user.setUsername(request.getUsername().trim());
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        return response;
    }
}

