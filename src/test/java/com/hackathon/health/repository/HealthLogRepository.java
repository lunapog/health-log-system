package com.hackathon.health.repository;

import com.hackathon.health.entity.HealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {
    List<HealthLog> findAllByOrderByLogDateDesc();
}
