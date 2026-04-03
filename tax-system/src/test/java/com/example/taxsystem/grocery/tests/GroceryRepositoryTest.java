package com.example.taxsystem.grocery.tests;

import com.example.taxsystem.grocery.domain.GroceryStatus;
import com.example.taxsystem.grocery.infrastructure.GroceryItemEntity;
import com.example.taxsystem.grocery.infrastructure.GroceryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class GroceryRepositoryTest {

    @Autowired
    private GroceryRepository groceryRepository;

    @Test
    void shouldFindItemsByStatusAndName() {
        GroceryItemEntity pending = buildEntity("bananas", GroceryStatus.PENDING);
        GroceryItemEntity done = buildEntity("banana bread", GroceryStatus.DONE);
        groceryRepository.save(pending);
        groceryRepository.save(done);

        List<GroceryItemEntity> doneItems = groceryRepository.findAllByStatusOrderByCreatedAtDesc(GroceryStatus.DONE);
        List<GroceryItemEntity> bananaItems = groceryRepository.findAllByNameContainingIgnoreCaseOrderByCreatedAtDesc("banana");

        assertThat(doneItems).hasSize(1);
        assertThat(doneItems.get(0).getName()).isEqualTo("banana bread");
        assertThat(bananaItems).hasSize(2);
    }

    private GroceryItemEntity buildEntity(String name, GroceryStatus status) {
        GroceryItemEntity entity = new GroceryItemEntity();
        entity.setName(name);
        entity.setStatus(status);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
