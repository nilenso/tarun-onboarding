package com.nilenso.swiftactivities.controllers;

import com.nilenso.swiftactivities.models.dtos.GeolocationDto;
import com.nilenso.swiftactivities.services.ActivityService;
import jakarta.validation.Valid;
import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> createActivity(@Valid @RequestBody StartActivityDto startActivityDto) {
        var activity = activityService.addActivity(startActivityDto);
        Map<String, UUID> response = new HashMap<String, UUID>();
        response.put("activityId", activity.getActivityId());
        return response;
    }

    @PostMapping("/{activityId}/log")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logGeolocationData(@PathVariable UUID activityId,
                                   @Valid @RequestBody GeolocationDto geolocationDto) {
        activityService.logGeolocationData(activityId, geolocationDto);
    }

    @PostMapping("/{activityId}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endActivity(@PathVariable UUID activityId) {
        activityService.endActivity(activityId);
    }
}
