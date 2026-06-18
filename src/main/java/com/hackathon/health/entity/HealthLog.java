package com.hackathon.health.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class HealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double sleepHours;
    private int stressLevel;
    private String riskLevel;

    // --- 建構子 ---
    public HealthLog() {}

    public HealthLog(double sleepHours, int stressLevel, String riskLevel) {
        this.sleepHours = sleepHours;
        this.stressLevel = stressLevel;
        this.riskLevel = riskLevel;
    }

    // --- Getters and Setters (缺一不可) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}