package com.example.corebankledger.config;

import com.example.corebankledger.dto.CreateAccountRequest;
import com.example.corebankledger.entity.AccountType;
import com.example.corebankledger.repository.AccountRepository;
import com.example.corebankledger.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataLoader {

    @Bean
    CommandLineRunner loadInitialData(AccountRepository accountRepository, AccountService accountService) {
        return args -> {
            if (accountRepository.count() > 0) {
                return;
            }

            CreateAccountRequest william = new CreateAccountRequest();
            william.setOwnerName("William");
            william.setType(AccountType.CHECKING);
            accountService.create(william);

            CreateAccountRequest maria = new CreateAccountRequest();
            maria.setOwnerName("Maria");
            maria.setType(AccountType.SAVINGS);
            accountService.create(maria);
        };
    }
}

