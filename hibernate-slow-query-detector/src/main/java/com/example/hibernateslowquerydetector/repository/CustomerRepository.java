package com.example.hibernateslowquerydetector.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hibernateslowquerydetector.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findTop20ByCity(String city);
}

