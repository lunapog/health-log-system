package com.hackathon.health.controller;

import com.hackathon.health.entity.HealthLog;
import com.hackathon.health.service.HealthLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-logs")
@CrossOrigin(origins = "*") // 允許跨域請求，防止黑客松前後端分離部署時踩坑
public class HealthLogController {

    private final HealthLogService service;

    // 透過建構子注入 Service
    public HealthLogController(HealthLogService service) {
        this.service = service;
    }

    /**
     * 1. 取得所有健康日誌
     * GET http://localhost:8080/api/health-logs
     */
    @GetMapping
    public ResponseEntity<List<HealthLog>> getAllLogs() {
        return ResponseEntity.ok(service.getAllLogs());
    }

    /**
     * 2. 新增一筆健康日誌（自動透過決策樹與動態門檻計算風險等級）
     * POST http://localhost:8080/api/health-logs
     */
    @PostMapping
    public ResponseEntity<HealthLog> createLog(@RequestBody HealthLog log) {
        // A. 呼叫 Service 的決策樹演算法，根據輸入的睡眠與壓力，動態計算風險
        String calculatedRisk = service.predictRisk(log.getSleepHours(), log.getStressLevel());
        
        // B. 將計算好的風險等級寫入實體
        log.setRiskLevel(calculatedRisk);
        
        // C. 儲存至資料庫
        HealthLog savedLog = service.saveLog(log);
        return ResponseEntity.ok(savedLog);
    }

    /**
     * 3. 額外加分端點：取得目前根據 90 天歷史資料算出的最佳睡眠時數門檻
     * GET http://localhost:8080/api/health-logs/best-threshold
     */
    @GetMapping("/best-threshold")
    public ResponseEntity<Double> getBestThreshold() {
        double threshold = service.findBestSleepThreshold();
        return ResponseEntity.ok(threshold);
    }
}