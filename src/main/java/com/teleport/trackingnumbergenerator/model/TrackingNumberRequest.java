package com.teleport.trackingnumbergenerator.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumberRequest {
    @NotNull
    @Pattern(regexp = "^[A-Z]{2}$")
    private String origin_country_id;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$")
    private String destination_country_id;

    @DecimalMax("999.999")
    private BigDecimal weight;

    @NotNull
    private String created_at;

    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    private String customer_id;

    @NotNull
    private String customer_name;

    @NotNull
    @Pattern(regexp = "^[a-z0-9-]+$")
    private String customer_slug;
}
