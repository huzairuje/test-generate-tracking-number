package com.teleport.trackingnumbergenerator.service.impl;

import com.teleport.trackingnumbergenerator.entity.EventLog;
import com.teleport.trackingnumbergenerator.entity.TrackingNumber;
import com.teleport.trackingnumbergenerator.model.TrackingNumberRequest;
import com.teleport.trackingnumbergenerator.model.TrackingNumberResponse;
import com.teleport.trackingnumbergenerator.repository.mongodb.TrackingNumberMongoRepository;
import com.teleport.trackingnumbergenerator.repository.postgresql.TrackingNumberRepository;
import com.teleport.trackingnumbergenerator.service.TrackingNumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TrackingNumberServiceImpl implements TrackingNumberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingNumberServiceImpl.class);
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();
    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    private final TrackingNumberRepository trackingNumberRepository;
    private final TrackingNumberMongoRepository trackingNumberMongoRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TrackingNumberServiceImpl(TrackingNumberRepository trackingNumberRepository,
                                     TrackingNumberMongoRepository trackingNumberMongoRepository,
                                     KafkaTemplate<String, String> kafkaTemplate) {
        this.trackingNumberRepository = trackingNumberRepository;
        this.trackingNumberMongoRepository = trackingNumberMongoRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest request) {
        try {
            long timestamp = Instant.now().toEpochMilli();
            long count = counter.getAndIncrement();
            String uniqueId = toBase62(timestamp) + toBase62(count);
            String trackingNumber = (request.getCustomer_slug().substring(0, 3) + uniqueId).toUpperCase();
            if (trackingNumber.length() > 16) {
                trackingNumber = trackingNumber.substring(0, 16);
            }
            if (!isValidUUID(request.getCustomer_id())) {
                throw new IllegalArgumentException("Invalid UUID for customer_id");
            }
            TrackingNumber tn = getTrackingNumber(request, trackingNumber);
            trackingNumberRepository.save(tn);
            trackingNumberMongoRepository.save(setLog(tn));
            kafkaTemplate.send("tracking-numbers", trackingNumber);

            LOGGER.info("Generated tracking number: {} for customer: {}", trackingNumber, request.getCustomer_slug());

            return setResponseTrackingNumber(tn);
        } catch (Exception e) {
            // Only respond with 409 if it's a DB constraint violation
            if (e.getCause() != null && e.getCause().getMessage().contains("duplicate key")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "TrackingNumber collision—please retry", e);
            }

            // Otherwise, return 500 or appropriate error
            LOGGER.error("Error generating trackingNumber", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error during tracking number generation", e);
        }
    }

    private static TrackingNumber getTrackingNumber(TrackingNumberRequest request, String trackingNumber) {
        TrackingNumber tn = new TrackingNumber();
        tn.setTrackingNumber(trackingNumber);
        tn.setCreatedAt(Instant.parse(request.getCreated_at()));
        tn.setOrigin_country_id(request.getOrigin_country_id());
        tn.setDestination_country_id(request.getDestination_country_id());
        tn.setWeight(request.getWeight());
        UUID customerUuid = UUID.fromString(request.getCustomer_id());
        tn.setCustomer_id(customerUuid);
        tn.setCustomer_name(request.getCustomer_name());
        tn.setCustomer_slug(request.getCustomer_slug());
        return tn;
    }

    private static TrackingNumberResponse setResponseTrackingNumber(TrackingNumber trackingNumber) {
        TrackingNumberResponse tnr = new TrackingNumberResponse();
        tnr.setTracking_number(trackingNumber.getTrackingNumber());
        tnr.setCreated_at(String.valueOf(trackingNumber.getCreatedAt()));
        tnr.setOrigin_country_id(trackingNumber.getOrigin_country_id());
        tnr.setDestination_country_id(trackingNumber.getDestination_country_id());
        tnr.setWeight(trackingNumber.getWeight());
        tnr.setCustomer_id(trackingNumber.getCustomer_id());
        tnr.setCustomer_name(trackingNumber.getCustomer_name());
        tnr.setCustomer_slug(trackingNumber.getCustomer_slug());
        return tnr;
    }

    private static EventLog setLog(TrackingNumber trackingNumber) {
        EventLog el = new EventLog();
        el.setCreatedAt(trackingNumber.getCreatedAt());
        el.setEvent(trackingNumber);
        return el;
    }

    private String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.reverse().toString();
    }

    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
