package com.example.guitarfactorysystem.repository;

import com.example.guitarfactorysystem.entity.WorkOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    boolean existsByCustomGuitarOrder_Id(Long customGuitarOrderId);

    @EntityGraph(attributePaths = {"customGuitarOrder", "customGuitarOrder.guitarModel", "customGuitarOrder.items", "customGuitarOrder.items.component"})
    Optional<WorkOrder> findById(Long id);

    @EntityGraph(attributePaths = {"customGuitarOrder", "customGuitarOrder.guitarModel"})
    List<WorkOrder> findAllByOrderByCreatedAtDescIdDesc();
}
