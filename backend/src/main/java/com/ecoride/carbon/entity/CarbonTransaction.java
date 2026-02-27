package com.ecoride.carbon.entity;

import com.ecoride.ride.entity.Ride;
import com.ecoride.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "carbon_transactions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ride_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(name = "carbon_saved_grams", nullable = false)
    private int carbonSavedGrams;

    @Column(name = "credits_earned", nullable = false)
    private int creditsEarned;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
