package com.foodanalyzer.backend.repository;

import com.foodanalyzer.backend.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByNameIgnoreCase(String name);
}