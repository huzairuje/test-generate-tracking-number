package com.teleport.trackingnumbergenerator.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "event_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLog {
    @Id
    private String id;

    private Object event;

    @NotNull
    private Instant createdAt;
}