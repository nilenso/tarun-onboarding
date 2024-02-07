package com.nilenso.swiftactivities.models.dtos;

import com.nilenso.swiftactivities.models.Activity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartActivityDto(@NotNull(message = "Activity 'name' missing")
                               @NotBlank(message = "Activity 'name' is empty")
                               String name,
                               @NotNull(message = "Activity 'type' missing")
                               Activity.ActivityType type) {
}
