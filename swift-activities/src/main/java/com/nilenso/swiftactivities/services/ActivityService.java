package com.nilenso.swiftactivities.services;

import com.nilenso.swiftactivities.models.Activity;
import com.nilenso.swiftactivities.models.dtos.StartActivityDto;

public interface ActivityService {
    public Activity addActivity(StartActivityDto startActivityDto);
}