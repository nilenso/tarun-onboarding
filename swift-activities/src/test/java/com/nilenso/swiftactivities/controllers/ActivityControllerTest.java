package com.nilenso.swiftactivities.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class ActivityControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldCreateAnActivity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/activity"))
                .andExpect(status().isCreated());
    }
}
