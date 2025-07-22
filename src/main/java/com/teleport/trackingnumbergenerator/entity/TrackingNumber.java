package com.teleport.trackingnumbergenerator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "tracking_number")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tracking_number")
public class TrackingNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tracking_number_seq")
    @SequenceGenerator(name = "tracking_number_seq", sequenceName = "tracking_number_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true) // Add unique constraint
    private String trackingNumber;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}$")
    private String origin_country_id;

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}$")
    private String destination_country_id;

    @DecimalMax("999.999")
    private BigDecimal weight;

    @NotNull
    @Column(name = "customer_id", columnDefinition = "UUID")
    private UUID customer_id;

    @NotNull
    private String customer_name;

    @NotNull
    @Pattern(regexp = "^[a-z0-9-]+$")
    private String customer_slug;
}
