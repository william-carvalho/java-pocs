package com.example.guitarfactorysystem.repository;

import com.example.guitarfactorysystem.entity.CustomGuitarOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomGuitarOrderRepository extends JpaRepository<CustomGuitarOrder, Long> {

    @EntityGraph(attributePaths = {"guitarModel", "items", "items.component", "workOrder"})
    Optional<CustomGuitarOrder> findById(Long id);

    @EntityGraph(attributePaths = {"guitarModel", "items", "items.component", "workOrder"})
    List<CustomGuitarOrder> findAllByOrderByCreatedAtDescIdDesc();
}
