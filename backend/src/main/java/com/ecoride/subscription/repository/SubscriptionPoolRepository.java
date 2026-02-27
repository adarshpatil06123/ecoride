package com.ecoride.subscription.repository;

import com.ecoride.subscription.entity.SubscriptionPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPoolRepository extends JpaRepository<SubscriptionPool, UUID> {

    @Query("SELECT sp FROM SubscriptionPool sp " +
           "WHERE sp.id IN (SELECT sm.poolId FROM SubscriptionMember sm WHERE sm.userId = :userId)")
    List<SubscriptionPool> findPoolsByMember(@Param("userId") UUID userId);

    List<SubscriptionPool> findByPickupZoneAndDayOfWeek(String pickupZone, int dayOfWeek);
}
