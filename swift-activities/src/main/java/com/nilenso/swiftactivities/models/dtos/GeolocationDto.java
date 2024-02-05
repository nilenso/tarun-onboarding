package com.nilenso.swiftactivities.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record GeolocationDto(@NotNull(message = "'latitude' missing")
                             Double latitude,
                             @NotNull(message = "'longitude' missing")
                             Double longitude,
                             @NotNull(message = "'accuracy' missing")
                             Double accuracy,
                             @NotNull(message = "'recordedAt' missing")
                             Instant recordedAt) {
}