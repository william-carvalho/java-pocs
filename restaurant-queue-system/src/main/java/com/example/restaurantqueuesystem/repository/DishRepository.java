package com.example.restaurantqueuesystem.repository;

import com.example.restaurantqueuesystem.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {

    boolean existsByNameIgnoreCase(String name);
}
