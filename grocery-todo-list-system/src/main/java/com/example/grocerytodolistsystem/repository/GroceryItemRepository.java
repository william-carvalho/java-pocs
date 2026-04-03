package com.example.grocerytodolistsystem.repository;

import com.example.grocerytodolistsystem.entity.GroceryItem;
import com.example.grocerytodolistsystem.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {

    List<GroceryItem> findByStatusOrderByCreatedAtAscIdAsc(ItemStatus status);
}
