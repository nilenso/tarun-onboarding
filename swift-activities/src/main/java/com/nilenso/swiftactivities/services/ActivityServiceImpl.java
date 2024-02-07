package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.exception.UnprocessableEntityException;
import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.ActivityGeolocationData;
import com.nilenso.swiftactivities.models.dtos.GeolocationDto;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;
import com.nilenso.swiftactivities.repositories.ActivityGeolocationDataRepository;
import com.nilenso.swiftactivities.repositories.ActivityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityGeolocationDataRepository geolocationDataRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivityGeolocationDataRepository geolocationDataRepository) {
        this.activityRepository = activityRepository;
        this.geolocationDataRepository = geolocationDataRepository;
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
        if (existingActivity.getEndTime() != null) {
            throw new UnprocessableEntityException("This activity has already ended");
        }
        existingActivity.setEndTime(Instant.now());
        activityRepository.save(existingActivity);
    }

    @Override
    public void logGeolocationData(UUID activityId, GeolocationDto geolocationDto) {
        var geolocationData = new ActivityGeolocationData();
        geolocationData.setData(geolocationDto);
        geolocationData.setActivityId(activityId);
        geolocationData.setRecordedAt(geolocationDto.recordedAt());
        geolocationDataRepository.save(geolocationData);
    }
}
