package com.ecoride.subscription.service;

import com.ecoride.common.exception.ApiException;
import com.ecoride.subscription.dto.CreateSubscriptionRequest;
import com.ecoride.subscription.dto.SubscriptionDto;
import com.ecoride.subscription.entity.SubscriptionMember;
import com.ecoride.subscription.entity.SubscriptionPool;
import com.ecoride.subscription.repository.SubscriptionMemberRepository;
import com.ecoride.subscription.repository.SubscriptionPoolRepository;
import com.ecoride.user.entity.User;
import com.ecoride.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] DAY_NAMES = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    private final SubscriptionPoolRepository poolRepository;
    private final SubscriptionMemberRepository memberRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubscriptionDto createPool(String creatorEmail, CreateSubscriptionRequest req) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        LocalTime time = LocalTime.parse(req.getDepartureTime(), TIME_FMT);

        SubscriptionPool pool = SubscriptionPool.builder()
                .pickupZone(req.getPickupZone())
                .departureTime(time)
                .dayOfWeek(req.getDayOfWeek())
                .createdBy(creator)
                .build();

        pool = poolRepository.save(pool);

        // Creator is automatically a member
        SubscriptionMember member = new SubscriptionMember(pool.getId(), creator.getId());
        memberRepository.save(member);

        return toDto(pool, 1);
    }

    @Transactional
    public SubscriptionDto joinPool(String userEmail, UUID poolId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> ApiException.notFound("User not found"));
        SubscriptionPool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> ApiException.notFound("Subscription pool not found"));

        if (memberRepository.existsByPoolIdAndUserId(poolId, user.getId())) {
            throw ApiException.conflict("Already a member of this pool");
        }

        memberRepository.save(new SubscriptionMember(poolId, user.getId()));

        long count = memberRepository.countByPoolId(poolId);
        return toDto(pool, count);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getMyPools(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        return poolRepository.findPoolsByMember(user.getId()).stream()
                .map(pool -> toDto(pool, memberRepository.countByPoolId(pool.getId())))
                .collect(Collectors.toList());
    }

    private SubscriptionDto toDto(SubscriptionPool pool, long memberCount) {
        int dow = pool.getDayOfWeek();
        return SubscriptionDto.builder()
                .id(pool.getId())
                .pickupZone(pool.getPickupZone())
                .departureTime(pool.getDepartureTime().format(TIME_FMT))
                .dayOfWeek(dow)
                .dayName(DOW_SAFE(dow))
                .createdBy(pool.getCreatedBy().getId())
                .memberCount(memberCount)
                .build();
    }

    private String DOW_SAFE(int dow) {
        return (dow >= 0 && dow < DAY_NAMES.length) ? DAY_NAMES[dow] : "Unknown";
    }
}
