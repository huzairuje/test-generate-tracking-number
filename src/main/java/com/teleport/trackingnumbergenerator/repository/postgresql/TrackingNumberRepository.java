package com.teleport.trackingnumbergenerator.repository.postgresql;

import com.teleport.trackingnumbergenerator.entity.TrackingNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, String> {
}
