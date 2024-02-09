package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.dtos.GeolocationDto;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;

import java.util.UUID;

public interface ActivityService {
    public Activity addActivity(StartActivityDto startActivityDto);

    public void endActivity(UUID activityId);

    public void logGeolocationData(UUID activityId, GeolocationDto geolocationDto);
}