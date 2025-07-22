package com.teleport.trackingnumbergenerator.controller;

import com.teleport.trackingnumbergenerator.model.TrackingNumberRequest;
import com.teleport.trackingnumbergenerator.service.TrackingNumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class TrackingNumberController {

    private final TrackingNumberService trackingNumberService;

    public TrackingNumberController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping("/next-tracking-number")
    public ResponseEntity<?> getNextTrackingNumber(@Valid TrackingNumberRequest request) {
        return ResponseEntity.ok(trackingNumberService.generateTrackingNumber(request));
    }

}
