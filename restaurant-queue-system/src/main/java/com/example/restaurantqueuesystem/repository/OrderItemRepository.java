package com.example.restaurantqueuesystem.repository;

import com.example.restaurantqueuesystem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
