package com.ecoride.prediction.service;

import com.ecoride.carbon.entity.CarbonTransaction;
import com.ecoride.carbon.repository.CarbonTransactionRepository;
import com.ecoride.common.exception.ApiException;
import com.ecoride.prediction.dto.PredictionDto;
import com.ecoride.ride.entity.Ride;
import com.ecoride.user.entity.User;
import com.ecoride.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final int MIN_RIDES_FOR_PATTERN = 2;

    private final CarbonTransactionRepository carbonTransactionRepository;
    private final UserRepository userRepository;

    /**
     * Analyze past ride history to detect recurring patterns.
     * Groups rides by (zone, dayOfWeek, hourSlot) and surfaces patterns
     * that appear at least MIN_RIDES_FOR_PATTERN times.
     */
    @Transactional(readOnly = true)
    public List<PredictionDto> getSuggestions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        List<CarbonTransaction> history = carbonTransactionRepository.findByUserId(user.getId());

        // Group by (zone, dayOfWeek, hour)
        Map<String, List<Ride>> patterns = new LinkedHashMap<>();
        for (CarbonTransaction tx : history) {
            Ride ride = tx.getRide();
            ZonedDateTime zdt = ride.getDepartureTime().atZone(IST);
            int hour = zdt.getHour();
            DayOfWeek dow = zdt.getDayOfWeek();
            String key = ride.getPickupZone() + "|" + dow.name() + "|" + hour;
            patterns.computeIfAbsent(key, k -> new ArrayList<>()).add(ride);
        }

        return patterns.entrySet().stream()
                .filter(e -> e.getValue().size() >= MIN_RIDES_FOR_PATTERN)
                .map(e -> {
                    String[] parts = e.getKey().split("\\|");
                    String zone = parts[0];
                    String dow  = parts[1];
                    String time = String.format("%02d:00", Integer.parseInt(parts[2]));
                    int count = e.getValue().size();
                    return PredictionDto.builder()
                            .pickupZone(zone)
                            .dayOfWeek(dow)
                            .suggestedTime(time)
                            .rideCount(count)
                            .message(String.format(
                                    "You've ridden from Zone %s on %ss around %s — %d times. Consider a recurring pool!",
                                    zone, capitalize(dow), time, count))
                            .build();
                })
                .sorted(Comparator.comparingInt(PredictionDto::getRideCount).reversed())
                .collect(Collectors.toList());
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
