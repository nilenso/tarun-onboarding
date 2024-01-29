package com.nilenso.swiftactivities.controllers;

import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.services.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc
class ActivityControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ActivityService activityService;

    @Test
    void createActivity() throws Exception {
        var requestJson = "{\"type\": \"Running\"}";
        var newActivityId = randomUUID();
        var responseJSON = "{\"activityId\":\"" + newActivityId + "\"}";
        var newActivity = new Activity();
        newActivity.setActivityId(newActivityId);
        when(activityService.addActivity(any())).thenReturn(newActivity);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/activity")
                .content(requestJson)
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJSON));
    }
}