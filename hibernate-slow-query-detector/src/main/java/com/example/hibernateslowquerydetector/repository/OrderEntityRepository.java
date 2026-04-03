package com.example.hibernateslowquerydetector.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hibernateslowquerydetector.entity.OrderEntity;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
}

