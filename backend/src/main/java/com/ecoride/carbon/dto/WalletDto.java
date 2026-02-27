package com.ecoride.carbon.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WalletDto {
    private int totalCredits;
    private long totalCarbonSavedGrams;
    private double totalCarbonSavedKg;
    private List<TransactionEntry> recentTransactions;

    @Data
    @Builder
    public static class TransactionEntry {
        private String rideId;
        private int carbonSavedGrams;
        private int creditsEarned;
        private String createdAt;
    }
}
