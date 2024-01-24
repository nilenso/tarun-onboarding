package com.nilenso.swiftactivities.controllers;

import com.nilenso.swiftactivities.domain.Activity;
import com.nilenso.swiftactivities.repositories.ActivityRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity newActivity) {
        return activityRepository.save(newActivity);
    }
}
