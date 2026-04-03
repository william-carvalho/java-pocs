package com.example.socialmediaphotoapp.config;

import com.example.socialmediaphotoapp.entity.Tag;
import com.example.socialmediaphotoapp.entity.User;
import com.example.socialmediaphotoapp.repository.TagRepository;
import com.example.socialmediaphotoapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataLoader {

    @Bean
    CommandLineRunner loadInitialData(UserRepository userRepository, TagRepository tagRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                saveUser(userRepository, "William Carvalho", "william");
                saveUser(userRepository, "Maria Silva", "maria");
                saveUser(userRepository, "João Souza", "joao");
            }

            if (tagRepository.count() == 0) {
                saveTag(tagRepository, "travel");
                saveTag(tagRepository, "food");
                saveTag(tagRepository, "beach");
                saveTag(tagRepository, "friends");
            }
        };
    }

    private void saveUser(UserRepository userRepository, String name, String username) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        userRepository.save(user);
    }

    private void saveTag(TagRepository tagRepository, String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
    }
}
