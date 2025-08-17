package com.foodanalyzer.backend.repository;

import com.foodanalyzer.backend.entity.User;
import com.foodanalyzer.backend.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findTop5ByUserOrderByTimestampDesc(User user);
    List<UserHistory> findByUserOrderByTimestampDesc(User user);
}