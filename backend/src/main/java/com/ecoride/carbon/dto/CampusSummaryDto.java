package com.ecoride.carbon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampusSummaryDto {
    private long totalCarbonSavedGrams;
    private double totalCarbonSavedKg;
    private long totalRidesCompleted;
    private long totalCarbonCreditsIssued;
}
