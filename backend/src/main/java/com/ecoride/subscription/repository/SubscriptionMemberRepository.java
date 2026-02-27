package com.ecoride.subscription.repository;

import com.ecoride.subscription.entity.SubscriptionMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionMemberRepository extends JpaRepository<SubscriptionMember, SubscriptionMember.SubscriptionMemberId> {
    boolean existsByPoolIdAndUserId(UUID poolId, UUID userId);
    long countByPoolId(UUID poolId);
}
