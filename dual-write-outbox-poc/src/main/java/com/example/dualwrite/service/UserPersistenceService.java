package com.example.dualwrite.service;

import com.example.dualwrite.api.RegistrationRequest;
import com.example.dualwrite.domain.UserAccount;
import com.example.dualwrite.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserPersistenceService {

    private final UserAccountRepository userRepository;

    public UserPersistenceService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserAccount createAndCommit(RegistrationRequest request) {
        UserAccount user = new UserAccount(UUID.randomUUID().toString(), request.getName(),
                request.getEmail(), Instant.now());
        return userRepository.saveAndFlush(user);
    }
}
