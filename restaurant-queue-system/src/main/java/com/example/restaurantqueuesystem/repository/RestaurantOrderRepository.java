package com.example.restaurantqueuesystem.repository;

import com.example.restaurantqueuesystem.entity.RestaurantOrder;
import com.example.restaurantqueuesystem.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {

    @EntityGraph(attributePaths = {"items", "items.dish"})
    Optional<RestaurantOrder> findWithItemsById(Long id);

    @EntityGraph(attributePaths = {"items", "items.dish"})
    List<RestaurantOrder> findByStatusInOrderByCreatedAtAscIdAsc(Collection<OrderStatus> statuses);
}
