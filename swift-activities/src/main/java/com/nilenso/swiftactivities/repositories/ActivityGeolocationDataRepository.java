package com.nilenso.swiftactivities.repositories;

import com.nilenso.swiftactivities.models.ActivityGeolocationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.UUID;

public interface ActivityGeolocationDataRepository extends JpaRepository<ActivityGeolocationData, UUID> {
}
