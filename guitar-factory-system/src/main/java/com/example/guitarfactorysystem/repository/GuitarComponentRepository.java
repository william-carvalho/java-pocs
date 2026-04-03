package com.example.guitarfactorysystem.repository;

import com.example.guitarfactorysystem.entity.GuitarComponent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuitarComponentRepository extends JpaRepository<GuitarComponent, Long> {
}
