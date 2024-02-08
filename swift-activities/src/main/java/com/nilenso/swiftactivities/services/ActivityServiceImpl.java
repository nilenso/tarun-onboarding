package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.exception.UnprocessableEntityException;
import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.ActivityGeolocationData;
import com.nilenso.swiftactivities.models.ActivityInsights;
import com.nilenso.swiftactivities.models.dtos.ActivityInsightsDto;
import com.nilenso.swiftactivities.models.dtos.GeolocationDto;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;
import com.nilenso.swiftactivities.repositories.ActivityGeolocationDataRepository;
import com.nilenso.swiftactivities.repositories.ActivityInsightsRepository;
import com.nilenso.swiftactivities.repositories.ActivityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityGeolocationDataRepository geolocationDataRepository;
    private final ActivityInsightsRepository activityInsightsRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivityGeolocationDataRepository geolocationDataRepository, ActivityInsightsRepository activityInsightsRepository) {
        this.activityRepository = activityRepository;
        this.geolocationDataRepository = geolocationDataRepository;
        this.activityInsightsRepository = activityInsightsRepository;
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

    private ActivityInsights generateAndSaveInsights(UUID activityId) {
        ActivityInsights activityInsights = new ActivityInsights();
        ArrayList<ActivityGeolocationData> activityGeolocationDataArrayList = geolocationDataRepository.findByActivityId(activityId);
        ActivityInsightsDto activityInsightsDto = generateInsights(activityGeolocationDataArrayList);
        activityInsights.setActivityId(activityId);
        activityInsights.setStats(activityInsightsDto);
        return activityInsightsRepository.save(activityInsights);
    }

    private ActivityInsightsDto generateInsights(ArrayList<ActivityGeolocationData> activityGeolocationDataArrayList) {
        activityGeolocationDataArrayList.sort(Comparator.comparing(ActivityGeolocationData::getRecordedAt));
        double maxSpeed = 0;
        double distance = 0;

        if (activityGeolocationDataArrayList.size() < 2) {
            return new ActivityInsightsDto(0.0, 0.0, 0.0, 0.0);
        } else {
            var lastGeolocation = activityGeolocationDataArrayList.get(0);
            for (int i = 1; i < activityGeolocationDataArrayList.size(); i++) {
                var lastGeolocationCoords = lastGeolocation.getData();
                var currentGeolocation = activityGeolocationDataArrayList.get(i);
                var currentGeolocationCoords = currentGeolocation.getData();
                double lat1 = Math.toRadians(lastGeolocationCoords.latitude()),
                        lng1 = Math.toRadians(lastGeolocationCoords.longitude()),
                        lat2 = Math.toRadians(currentGeolocationCoords.latitude()),
                        lng2 = Math.toRadians(currentGeolocationCoords.longitude());

                double delta = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                        + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1)) * 6371;

                distance += delta;
                Duration timeDiff = Duration.between(lastGeolocation.getRecordedAt(), currentGeolocation.getRecordedAt());
                var speed = delta / timeDiff.getSeconds();
                maxSpeed = Math.max(maxSpeed, speed);

                lastGeolocation = currentGeolocation;
            }

            var startGeolocation = activityGeolocationDataArrayList.get(0);
            Duration timeDiff = Duration.between(startGeolocation.getRecordedAt(), lastGeolocation.getRecordedAt());
            double averageSpeed = distance / timeDiff.getSeconds();
            return new ActivityInsightsDto((double) timeDiff.getSeconds(), distance, maxSpeed, averageSpeed);
        }
    }

    @Override
    public ActivityInsightsDto activityInsights(UUID activityId) {
        ActivityInsights activityInsights = activityInsightsRepository.findById(activityId).orElse(generateAndSaveInsights(activityId));
        var stats = activityInsights.getStats();
        return new ActivityInsightsDto(stats.duration(), stats.distance(), stats.maxSpeed(), stats.averageSpeed());
    }
}
