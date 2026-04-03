package com.example.guitarfactorysystem.repository;

import com.example.guitarfactorysystem.entity.GuitarModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuitarModelRepository extends JpaRepository<GuitarModel, Long> {

    boolean existsByNameIgnoreCase(String name);
}
