package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;
import com.nilenso.swiftactivities.repositories.ActivityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Activity addActivity(StartActivityDto startActivityDto) {
        var activity = new Activity();
        Instant currentUtcTime = Instant.now();
        activity.setName(startActivityDto.name());
        activity.setType(startActivityDto.type());
        activity.setStartTime(currentUtcTime);
        return activityRepository.save(activity);
    }

    @Override
    public void endActivity(UUID activityId) {
        Activity existingActivity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find the activity"));
        existingActivity.setEndTime(Instant.now());
        activityRepository.save(existingActivity);
    }
}
