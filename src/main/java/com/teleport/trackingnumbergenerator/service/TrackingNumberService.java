package com.teleport.trackingnumbergenerator.service;

import com.teleport.trackingnumbergenerator.model.TrackingNumberRequest;
import com.teleport.trackingnumbergenerator.model.TrackingNumberResponse;

public interface TrackingNumberService {
    TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest request);
}
