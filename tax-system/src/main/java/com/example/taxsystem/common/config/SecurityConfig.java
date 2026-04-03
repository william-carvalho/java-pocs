package com.example.taxsystem.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("deprecation")
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/grocery/**", "/api/stress-tests/reports/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/grocery/**", "/api/stress-tests/**").hasRole("WRITER")
                .antMatchers(HttpMethod.PUT, "/api/grocery/**").hasRole("WRITER")
                .antMatchers(HttpMethod.DELETE, "/api/grocery/**").hasRole("WRITER")
                .anyRequest().authenticated()
                .and()
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(ShowcaseProperties properties) {
        return new InMemoryUserDetailsManager(
                User.withUsername(properties.getSecurity().getReaderUser())
                        .password(properties.getSecurity().getReaderPassword())
                        .roles("READER")
                        .build(),
                User.withUsername(properties.getSecurity().getWriterUser())
                        .password(properties.getSecurity().getWriterPassword())
                        .roles("READER", "WRITER")
                        .build()
        );
    }

    @Bean
    public NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
