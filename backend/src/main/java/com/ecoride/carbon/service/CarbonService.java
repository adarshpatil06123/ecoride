package com.ecoride.carbon.service;

import com.ecoride.carbon.dto.CampusSummaryDto;
import com.ecoride.carbon.dto.WalletDto;
import com.ecoride.carbon.entity.CarbonTransaction;
import com.ecoride.carbon.repository.CarbonTransactionRepository;
import com.ecoride.common.exception.ApiException;
import com.ecoride.ride.entity.Ride;
import com.ecoride.ride.repository.RideRepository;
import com.ecoride.user.entity.User;
import com.ecoride.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarbonService {

    private static final int ASSUMED_DISTANCE_KM = 10;  // default campus commute distance
    private static final DateTimeFormatter FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.of("Asia/Kolkata"));

    private final CarbonTransactionRepository carbonTransactionRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    @Value("${ecoride.carbon.savings-per-km-grams}")
    private int savingsPerKmGrams;

    /**
     * Record carbon saving for a single user on a completed ride.
     * Idempotent — skipped if already credited.
     */
    @Transactional
    public void recordCarbonSaving(User user, Ride ride) {
        if (carbonTransactionRepository.existsByUserIdAndRideId(user.getId(), ride.getId())) {
            return; // idempotency guard
        }

        int carbonSaved = ASSUMED_DISTANCE_KM * savingsPerKmGrams;
        int credits = carbonSaved / 100;  // 1 credit per 100g saved

        CarbonTransaction tx = CarbonTransaction.builder()
                .user(user)
                .ride(ride)
                .carbonSavedGrams(carbonSaved)
                .creditsEarned(credits)
                .build();

        carbonTransactionRepository.save(tx);

        user.setCarbonCredits(user.getCarbonCredits() + credits);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public WalletDto getWallet(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        List<CarbonTransaction> txList = carbonTransactionRepository.findByUserId(user.getId());
        long totalCarbon = carbonTransactionRepository.sumCarbonSavedByUser(user.getId());

        List<WalletDto.TransactionEntry> entries = txList.stream()
                .map(tx -> WalletDto.TransactionEntry.builder()
                        .rideId(tx.getRide().getId().toString())
                        .carbonSavedGrams(tx.getCarbonSavedGrams())
                        .creditsEarned(tx.getCreditsEarned())
                        .createdAt(FMT.format(tx.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());

        return WalletDto.builder()
                .totalCredits(user.getCarbonCredits())
                .totalCarbonSavedGrams((int) totalCarbon)
                .totalCarbonSavedKg(totalCarbon / 1000.0)
                .recentTransactions(entries)
                .build();
    }

    @Transactional(readOnly = true)
    public CampusSummaryDto getCampusSummary() {
        long totalCarbon = carbonTransactionRepository.sumAllCarbonSaved();
        long totalRides = rideRepository.count();

        return CampusSummaryDto.builder()
                .totalCarbonSavedGrams(totalCarbon)
                .totalCarbonSavedKg(totalCarbon / 1000.0)
                .totalRidesCompleted(totalRides)
                .totalCarbonCreditsIssued(totalCarbon / 100)
                .build();
    }
}
