package com.nilenso.swiftactivities.controllers;

import com.nilenso.swiftactivities.services.HealthCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping("/api/health-check")
    public String healthCheck() {
        return healthCheckService.healthCheck();
    }
}