package com.ecoride.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "subscription_members")
@IdClass(SubscriptionMember.SubscriptionMemberId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionMember {

    @Id
    @Column(name = "pool_id", nullable = false)
    private UUID poolId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class SubscriptionMemberId implements Serializable {
        private UUID poolId;
        private UUID userId;
    }
}
