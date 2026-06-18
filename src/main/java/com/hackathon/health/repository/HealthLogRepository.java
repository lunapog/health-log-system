package com.hackathon.health.repository;

import com.hackathon.health.entity.HealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {
    // 保持裡面完全空白！
    // JpaRepository 內建的 findAll() 和 save() 就完全夠我們用了！
}