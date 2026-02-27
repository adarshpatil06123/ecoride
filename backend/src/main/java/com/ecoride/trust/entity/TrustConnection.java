package com.ecoride.trust.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "trust_connections")
@IdClass(TrustConnectionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrustConnection {

    @Id
    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;

    @Id
    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;

    @Column(name = "mutual_ride_count", nullable = false)
    @Builder.Default
    private int mutualRideCount = 1;
}
