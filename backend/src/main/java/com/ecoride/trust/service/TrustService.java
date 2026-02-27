package com.ecoride.trust.service;

import com.ecoride.common.exception.ApiException;
import com.ecoride.trust.dto.TrustProfileDto;
import com.ecoride.trust.entity.TrustConnection;
import com.ecoride.trust.entity.TrustConnectionId;
import com.ecoride.trust.repository.TrustConnectionRepository;
import com.ecoride.user.entity.User;
import com.ecoride.user.repository.UserRepository;
import com.ecoride.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrustService {

    private final TrustConnectionRepository trustConnectionRepository;
    private final UserRepository userRepository;

    /**
     * Called after a ride completes — updates or creates the mutual connection row.
     * Enforces canonical ordering: user1_id < user2_id.
     */
    @Transactional
    public void recordSharedRide(UUID a, UUID b) {
        UUID lo = a.compareTo(b) < 0 ? a : b;
        UUID hi = a.compareTo(b) < 0 ? b : a;

        trustConnectionRepository.findBetween(lo, hi).ifPresentOrElse(tc -> {
            tc.setMutualRideCount(tc.getMutualRideCount() + 1);
            trustConnectionRepository.save(tc);
        }, () -> {
            TrustConnection tc = TrustConnection.builder()
                    .user1Id(lo)
                    .user2Id(hi)
                    .mutualRideCount(1)
                    .build();
            trustConnectionRepository.save(tc);
        });
    }

    @Transactional(readOnly = true)
    public TrustProfileDto getTrustProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        List<TrustConnection> connections = trustConnectionRepository.findAllForUser(userId);

        return TrustProfileDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .trustScore(user.getTrustScore())
                .badge(UserService.resolveBadge(user.getTrustScore()))
                .ridesCompleted(user.getRidesCompleted())
                .uniqueRidePartners(connections.size())
                .build();
    }
}
