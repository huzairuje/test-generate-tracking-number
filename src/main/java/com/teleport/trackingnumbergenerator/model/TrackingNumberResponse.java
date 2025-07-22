package com.teleport.trackingnumbergenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumberResponse {
    private String tracking_number;
    private String created_at;
    private String origin_country_id;
    private String destination_country_id;
    private BigDecimal weight;
    private UUID customer_id;
    private String customer_name;
    private String customer_slug;
}
