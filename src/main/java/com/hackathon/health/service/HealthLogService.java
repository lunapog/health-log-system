package com.hackathon.health.service;

import com.hackathon.health.entity.HealthLog;
import com.hackathon.health.repository.HealthLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HealthLogService {

    private final HealthLogRepository repository;

    // 透過建構子注入 Repository
    public HealthLogService(HealthLogRepository repository) {
        this.repository = repository;
    }

    /**
     * 1. 取得所有健康日誌
     */
    public List<HealthLog> getAllLogs() {
        return repository.findAll();
    }

    /**
     * 2. 儲存或更新健康日誌
     */
    public HealthLog saveLog(HealthLog log) {
        return repository.save(log);
    }

    /**
     * 3. 核心決策樹：動態預測風險等級
     * 如果資料庫有足夠資料，會使用資訊增益算出的動態門檻；若無則使用預設值。
     */
    public String predictRisk(double sleepHours, int stressLevel) {
        // 動態取得當前最優的睡眠門檻（基礎值預設為 6.5）
        double dynamicSleepThreshold = findBestSleepThreshold();

        // 決策樹第一層：睡眠時數
        if (sleepHours <= dynamicSleepThreshold) {
            // 決策樹第二層：壓力指數
            if (stressLevel >= 7) {
                return "HIGH"; // 睡眠少且壓力大 -> 高風險
            } else {
                return "MID";  // 睡眠少但壓力適中 -> 中風險
            }
        } else {
            if (stressLevel >= 8) {
                return "MID";  // 睡眠充足但壓力極大 -> 中風險
            } else {
                return "LOW";  // 睡眠充足且壓力低 -> 低風險
            }
        }
    }

    /**
     * 4. 機器學習演算法：自動尋找最佳睡眠時數門檻
     * 透過計算資訊增益 (Information Gain)，找出最能區分高/低風險的切分點
     */
    public double findBestSleepThreshold() {
        List<HealthLog> logs = repository.findAll();
        // 如果沒有資料，預設以 6.5 小時作為基準門檻
        if (logs.isEmpty()) return 6.5; 

        double bestGain = -1;
        double bestThreshold = 6.5;

        // A. 計算切分前，整體的資料混亂度 (Total Entropy)
        double totalEntropy = calculateTotalEntropy(logs);

        // B. 穷举法：嘗試從 4.0 到 9.0 小時，每 0.5 小時測試一次當作切分門檻
        for (double t = 4.0; t <= 9.0; t += 0.5) {
            double currentGain = calculateInformationGain(logs, t, totalEntropy);
            
            // C. 尋找能帶來最大資訊增益（讓兩邊資料最純淨）的門檻
            if (currentGain > bestGain) {
                bestGain = currentGain;
                bestThreshold = t;
            }
        }

        return bestThreshold;
    }

    /**
     * 輔助方法：計算 Entropy (熵)
     */
    private double calculateEntropy(int highRiskCount, int lowRiskCount) {
        int total = highRiskCount + lowRiskCount;
        if (total == 0 || highRiskCount == 0 || lowRiskCount == 0) return 0.0;

        double pHigh = (double) highRiskCount / total;
        double pLow = (double) lowRiskCount / total;

        // 使用換底公式計算 log2: log2(x) = log(x) / log(2)
        return - (pHigh * (Math.log(pHigh) / Math.log(2)) + pLow * (Math.log(pLow) / Math.log(2)));
    }

    /**
     * 輔助方法：計算原始總體的 Entropy
     */
    private double calculateTotalEntropy(List<HealthLog> logs) {
        int high = 0, low = 0;
        for (HealthLog log : logs) {
            if ("HIGH".equals(log.getRiskLevel())) high++;
            if ("LOW".equals(log.getRiskLevel())) low++;
        }
        return calculateEntropy(high, low);
    }

    /**
     * 輔助方法：計算特定門檻下的資訊增益 (Information Gain)
     */
    private double calculateInformationGain(List<HealthLog> logs, double threshold, double totalEntropy) {
        int leftHigh = 0, leftLow = 0;   // 睡眠時數 <= 門檻 (較少睡眠組)
        int rightHigh = 0, rightLow = 0; // 睡眠時數 > 門檻  (較多睡眠組)

        for (HealthLog log : logs) {
            // 排除中風險，專注看這個門檻能不能完美區分「高」與「低」
            if ("MID".equals(log.getRiskLevel())) continue;

            if (log.getSleepHours() <= threshold) {
                if ("HIGH".equals(log.getRiskLevel())) leftHigh++;
                else leftLow++;
            } else {
                if ("HIGH".equals(log.getRiskLevel())) rightHigh++;
                else rightLow++;
            }
        }

        int leftTotal = leftHigh + leftLow;
        int rightTotal = rightHigh + rightLow;
        int total = leftTotal + rightTotal;

        if (total == 0) return 0.0;

        // 計算左、右分支各自的 Entropy
        double leftEntropy = calculateEntropy(leftHigh, leftLow);
        double rightEntropy = calculateEntropy(rightHigh, rightLow);

        // 加權計算切分後的殘餘混亂度 (Remainder)
        double remainder = ((double) leftTotal / total) * leftEntropy + ((double) rightTotal / total) * rightEntropy;

        // 資訊增益 = 原本混亂度 - 切分後混亂度
        return totalEntropy - remainder;
    }
}