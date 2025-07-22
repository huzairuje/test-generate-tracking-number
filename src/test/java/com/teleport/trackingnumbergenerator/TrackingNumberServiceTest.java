package com.teleport.trackingnumbergenerator;

import com.teleport.trackingnumbergenerator.entity.TrackingNumber;
import com.teleport.trackingnumbergenerator.model.TrackingNumberRequest;
import com.teleport.trackingnumbergenerator.model.TrackingNumberResponse;
import com.teleport.trackingnumbergenerator.repository.mongodb.TrackingNumberMongoRepository;
import com.teleport.trackingnumbergenerator.repository.postgresql.TrackingNumberRepository;
import com.teleport.trackingnumbergenerator.service.impl.TrackingNumberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrackingNumberServiceTest {

    @Mock
    private TrackingNumberRepository trackingNumberRepository;

    @Mock
    private TrackingNumberMongoRepository trackingNumberMongoRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TrackingNumberServiceImpl trackingNumberService;

    @Captor
    private ArgumentCaptor<TrackingNumber> trackingNumberCaptor;

    private TrackingNumberRequest request;

    @BeforeEach
    public void setup() {
        request = new TrackingNumberRequest(
                "MY",
                "ID",
                new BigDecimal("1.234"),
                "2018-11-20T19:29:32+08:00",
                "de619854-b59b-425e-9db4-943979e1bd49",
                "RedBox Logistics",
                "redbox-logistics"
        );
    }

    private CompletableFuture<SendResult<String, String>> createCompletedFuture() {
        return CompletableFuture.completedFuture(new SendResult<>(null, null));
    }

    @Test
    public void whenGenerateTrackingNumber_thenReturnsValidResponse() {
        // Mock repository saves
        when(trackingNumberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackingNumberMongoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock Kafka to return completed future
        when(kafkaTemplate.send(any(), any())).thenReturn(createCompletedFuture());

        // Act
        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTracking_number());
        assertNotNull(response.getCreated_at());
        assertTrue(response.getTracking_number().matches("^[A-Z0-9]{1,16}$"));
        assertTrue(response.getTracking_number().startsWith("RED"));
    }

    @Test
    public void whenGenerateTrackingNumber_thenSavesToRepository() {
        // Mock Kafka to return completed future
        when(kafkaTemplate.send(any(), any())).thenReturn(createCompletedFuture());

        when(trackingNumberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackingNumberMongoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        trackingNumberService.generateTrackingNumber(request);

        // Assert
        verify(trackingNumberRepository).save(trackingNumberCaptor.capture());
        TrackingNumber saved = trackingNumberCaptor.getValue();

        assertNotNull(saved.getTrackingNumber());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getTrackingNumber().startsWith("RED"));
    }

    @Test
    public void whenGenerateTrackingNumber_thenSendsKafkaMessage() {
        // Mock repository saves
        when(trackingNumberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackingNumberMongoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock Kafka to return completed future
        when(kafkaTemplate.send(any(), any())).thenReturn(createCompletedFuture());

        // Act
        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        // Assert
        verify(kafkaTemplate).send(eq("tracking-numbers"), eq(response.getTracking_number()));
    }

    @Test
    public void whenCustomerSlugShort_thenStillWorks() {
        // Arrange
        request.setCustomer_slug("ab");

        // Mock Kafka to return completed future
        when(kafkaTemplate.send(any(), any())).thenReturn(createCompletedFuture());

        when(trackingNumberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackingNumberMongoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        // Assert
        assertTrue(response.getTracking_number().startsWith("AB"));
    }

    @Test
    public void whenInvalidCustomerId_thenThrowsException() {
        // Arrange
        request.setCustomer_id("invalid-uuid");

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            trackingNumberService.generateTrackingNumber(request);
        });
    }

    @Test
    public void whenDuplicateTrackingNumber_thenThrowsConflict() {
        // Arrange
        // Simulate duplicate key violation
        when(trackingNumberRepository.save(any()))
                .thenThrow(new RuntimeException("duplicate key"));

        // Mock Kafka to return completed future
        when(kafkaTemplate.send(any(), any())).thenReturn(createCompletedFuture());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            trackingNumberService.generateTrackingNumber(request);
        }, "Should throw 409 CONFLICT for duplicate tracking numbers");
    }
}