package com.example.taxsystem.grocery.infrastructure;

import com.example.taxsystem.grocery.domain.GroceryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryRepository extends JpaRepository<GroceryItemEntity, Long> {

    List<GroceryItemEntity> findAllByStatusOrderByCreatedAtDesc(GroceryStatus status);

    List<GroceryItemEntity> findAllByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);
}
