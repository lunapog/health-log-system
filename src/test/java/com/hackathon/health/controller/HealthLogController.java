package com.hackathon.health.controller;

import com.hackathon.health.entity.HealthLog;
import com.hackathon.health.service.HealthLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-logs")
public class HealthLogController {

    private final HealthLogService service;

    public HealthLogController(HealthLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<HealthLog> getAll() {
        return service.getAllLogs();
    }

    @PostMapping
    public HealthLog create(@RequestBody HealthLog log) {
        return service.saveLog(log);
    }

    @PutMapping("/{id}")
    public HealthLog update(@PathVariable Long id, @RequestBody HealthLog log) {
        return service.updateLog(id, log);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteLog(id);
    }
}
