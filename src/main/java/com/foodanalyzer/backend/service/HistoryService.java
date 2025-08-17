package com.foodanalyzer.backend.service;

import com.foodanalyzer.backend.entity.Food;
import com.foodanalyzer.backend.entity.User;
import com.foodanalyzer.backend.entity.UserHistory;
import com.foodanalyzer.backend.model.FoodHistoryResponse;
import com.foodanalyzer.backend.repository.UserHistoryRepository;
import com.foodanalyzer.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {
    @Autowired
    private UserHistoryRepository historyRepository;
    @Autowired
    private UserRepository userRepository;

    public void addHistory(String username, Food food) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        UserHistory history = new UserHistory();
        history.setUser(user);
        history.setFood(food);
        history.setTimestamp(LocalDateTime.now());
        historyRepository.save(history);

        // Keep only the last 5 history entries
        List<UserHistory> all = historyRepository.findByUserOrderByTimestampDesc(user);
        if (all.size() > 5) {
            for (int i = 5; i < all.size(); i++) {
                historyRepository.delete(all.get(i));
            }
        }
    }

    public List<FoodHistoryResponse> getHistory(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return historyRepository.findTop5ByUserOrderByTimestampDesc(user)
                .stream()
                .map(history -> new FoodHistoryResponse(history.getFood(), history.getTimestamp()))
                .toList();
    }
}