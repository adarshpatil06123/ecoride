package com.ecoride.prediction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PredictionDto {
    private String pickupZone;
    private String suggestedTime;   // "HH:mm"
    private String dayOfWeek;       // "MONDAY" etc.
    private int rideCount;          // how many past rides matched this pattern
    private String message;         // human-readable suggestion
}
