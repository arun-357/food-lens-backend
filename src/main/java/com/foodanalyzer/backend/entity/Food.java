package com.foodanalyzer.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Food {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private String ingredients;
    private String benefits;
    private String imageUrl;
    private boolean isFood;

    public void setIsFood(boolean food) {
        this.isFood = food;
    }
}