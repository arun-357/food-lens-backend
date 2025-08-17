package com.foodanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class UserUsage {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;
    private LocalDate date;
    private int count;
}