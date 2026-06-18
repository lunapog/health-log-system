package com.hackathon.health.service;

import com.hackathon.health.entity.HealthLog;
import com.hackathon.health.repository.HealthLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthLogService {

    private final HealthLogRepository repository;

    public HealthLogService(HealthLogRepository repository) {
        this.repository = repository;
    }

    public List<HealthLog> getAllLogs() {
        return repository.findAllByOrderByLogDateDesc();
    }

    public HealthLog saveLog(HealthLog log) {
        // 寫入前，觸發多層決策樹計算風險等級
        log.setRiskLevel(calculateRiskLevel(log.getSleepHours(), log.getSteps(), log.getMoodScore()));
        return repository.save(log);
    }

    public HealthLog updateLog(Long id, HealthLog updatedLog) {
        return repository.findById(id).map(log -> {
            log.setLogDate(updatedLog.getLogDate());
            log.setSleepHours(updatedLog.getSleepHours());
            log.setSteps(updatedLog.getSteps());
            log.setMoodScore(updatedLog.getMoodScore());
            // 數據更新，重新計算風險
            log.setRiskLevel(calculateRiskLevel(log.getSleepHours(), log.getSteps(), log.getMoodScore()));
            return repository.save(log);
        }).orElseThrow(() -> new RuntimeException("Log not found"));
    }

    public void deleteLog(Long id) {
        repository.deleteById(id);
    }

    /**
     * 多層分支決策樹邏輯
     */
    private String calculateRiskLevel(double sleep, int steps, int mood) {
        // 第一層判斷：睡眠時間
        if (sleep < 6.0) {
            // 第二層判斷：步數
            if (steps < 5000) {
                // 第三層判斷：心情分數
                if (mood < 5) {
                    return "High"; // 睡少、動少、心情差
                } else {
                    return "Medium"; // 睡少、動少，但心情還可以
                }
            } else {
                if (mood < 5) {
                    return "High"; // 睡少、心情差，雖然有動但風險仍高
                } else {
                    return "Medium"; // 睡少，但步數夠且心情好
                }
            }
        } else if (sleep >= 7.0) {
            // 第二層判斷：步數
            if (steps >= 6000) {
                // 第三層判斷：心情分數
                if (mood >= 6) {
                    return "Low"; // 睡飽、動多、心情好
                } else {
                    return "Medium"; // 睡飽、動多，但心情異常差
                }
            } else {
                if (mood >= 6) {
                    return "Medium"; // 睡飽心情好，但缺乏運動
                } else {
                    return "Medium"; // 睡飽但不動且心情差
                }
            }
        } else {
            // 睡眠介於 6.0 ~ 7.0 之間
            if (steps < 4000 && mood < 5) {
                return "High"; // 睡眠普通，但極度缺乏運動且心情糟
            }
            return "Medium"; // 其他一般混合情況
        }
    }
}
