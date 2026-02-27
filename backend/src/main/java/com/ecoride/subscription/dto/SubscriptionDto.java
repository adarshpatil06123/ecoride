package com.ecoride.subscription.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SubscriptionDto {
    private UUID id;
    private String pickupZone;
    private String departureTime;
    private int dayOfWeek;
    private String dayName;
    private UUID createdBy;
    private long memberCount;
}
