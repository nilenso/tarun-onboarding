package com.nilenso.swiftactivities.models.dtos;

import com.nilenso.swiftactivities.models.Activity;
import jakarta.validation.constraints.NotNull;

public record StartActivityDto(String name,
                               @NotNull(message = "Activity 'type' missing")
                               Activity.ActivityType type) { }
