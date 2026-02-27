package com.ecoride.trust.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TrustProfileDto {
    private UUID userId;
    private String name;
    private int trustScore;
    private String badge;           // BRONZE, SILVER, GOLD, PLATINUM
    private int ridesCompleted;
    private int uniqueRidePartners;
}
