package com.foodanalyzer.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;
import jakarta.persistence.Column;

@Entity
@Data
public class Food {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String ingredients;
    @Column(columnDefinition = "TEXT")
    private String benefits;
    // Allow longer URLs
    @Column(length = 1024)
    private String imageUrl;
    // (valid for 24 hours)
    private LocalDateTime imageUrlExpiresAt;
    private boolean isFood;

    public void setIsFood(boolean food) {
        this.isFood = food;
    }
}