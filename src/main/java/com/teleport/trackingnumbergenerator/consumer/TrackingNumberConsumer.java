package com.teleport.trackingnumbergenerator.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TrackingNumberConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingNumberConsumer.class);

    @KafkaListener(topics = "tracking-numbers", groupId = "tracking-number-group")
    public void consume(String message) {
        LOGGER.info("Consumed message: {}", message);
    }
}
