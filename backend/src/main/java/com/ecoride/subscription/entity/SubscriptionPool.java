package com.ecoride.subscription.entity;

import com.ecoride.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_pools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPool {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pickup_zone", nullable = false, length = 50)
    private String pickupZone;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;  // 0=Sun, 6=Sat

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
