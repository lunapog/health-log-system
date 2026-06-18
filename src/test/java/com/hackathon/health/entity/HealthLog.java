package com.hackathon.health.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "health_logs")
public class HealthLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate logDate;
    private double sleepHours;
    private int steps;
    private int moodScore;
    private String riskLevel;
}
