package com.foodanalyzer.backend.model;

import com.foodanalyzer.backend.entity.Food;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FoodHistoryResponse {
    private Food food;
    private LocalDateTime timestamp;
}
