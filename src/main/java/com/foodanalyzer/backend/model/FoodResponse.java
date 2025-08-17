package com.foodanalyzer.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FoodResponse {
    @JsonProperty("isFood")
    private boolean isFood;
    private String info;
    private List<String> ingredients;
    private List<String> benefits;

    public FoodResponse(String name, String info, List<String> ingredients, List<String> benefits, String imageUrl) {
        this.isFood = true;
        this.info = info;
        this.ingredients = ingredients;
        this.benefits = benefits;
    }
}