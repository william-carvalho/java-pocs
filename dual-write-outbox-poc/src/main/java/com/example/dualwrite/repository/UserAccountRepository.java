package com.example.dualwrite.repository;

import com.example.dualwrite.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    List<UserAccount> findAllByOrderByCreatedAtAsc();
}
