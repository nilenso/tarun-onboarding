package com.nilenso.swiftactivities.repositories;

import com.nilenso.swiftactivities.models.ActivityGeolocationData;
import com.nilenso.swiftactivities.models.ActivityInsights;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ActivityGeolocationDataRepository extends JpaRepository<ActivityGeolocationData, UUID> {
    ArrayList<ActivityGeolocationData> findByActivityId(UUID activityId);
}
