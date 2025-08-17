package com.foodanalyzer.backend.repository;

import com.foodanalyzer.backend.entity.User;
import com.foodanalyzer.backend.entity.UserUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDate;

public interface UserUsageRepository extends JpaRepository<UserUsage, Long> {
    Optional<UserUsage> findByUserAndDate(User user, LocalDate date);
}