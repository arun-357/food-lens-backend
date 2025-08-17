package com.foodanalyzer.backend.controller;

import com.foodanalyzer.backend.entity.Food;
import com.foodanalyzer.backend.model.FoodHistoryResponse;
import com.foodanalyzer.backend.service.FoodService;
import com.foodanalyzer.backend.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private FoodService foodService;
    @Autowired
    private HistoryService historyService;

    @GetMapping("/search")
    public ResponseEntity<Food> search(@RequestParam String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            }
        }

        Food food = foodService.searchFood(name, username);

        if (username != null) {
            historyService.addHistory(username, food);
        }

        return ResponseEntity.ok(food);
    }

    @GetMapping("/history")
    public ResponseEntity<List<FoodHistoryResponse>> getHistory() {
        String username = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getUsername();

        return ResponseEntity.ok(historyService.getHistory(username));
    }
}