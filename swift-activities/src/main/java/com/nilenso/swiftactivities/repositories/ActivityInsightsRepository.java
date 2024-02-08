package com.nilenso.swiftactivities.repositories;

import com.nilenso.swiftactivities.models.ActivityInsights;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityInsightsRepository extends JpaRepository<ActivityInsights, UUID> {
}
