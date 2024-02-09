package com.nilenso.swiftactivities.controllers;

import com.nilenso.swiftactivities.services.HealthCheckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
@AutoConfigureMockMvc
class HealthCheckControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    HealthCheckService healthCheckService;

    @Test
    void healthCheck() throws Exception {
        var response = "OK";

        when(healthCheckService.healthCheck()).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/health-check"))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
}