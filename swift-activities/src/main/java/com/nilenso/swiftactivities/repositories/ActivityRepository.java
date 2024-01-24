package com.nilenso.swiftactivities.repositories;

import com.nilenso.swiftactivities.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}
