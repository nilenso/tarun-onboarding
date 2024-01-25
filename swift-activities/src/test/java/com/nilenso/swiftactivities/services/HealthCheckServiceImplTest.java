package com.nilenso.swiftactivities.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckServiceImplTest {

    @InjectMocks
    HealthCheckServiceImpl healthCheckService;

    @Test
    void healthCheck() {
        assertEquals(healthCheckService.healthCheck(), "OK");
    }
}