package com.ecoride.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 100)
    private String department;

    private Integer year;

    @Column(name = "trust_score", nullable = false)
    @Builder.Default
    private int trustScore = 0;

    @Column(name = "rides_completed", nullable = false)
    @Builder.Default
    private int ridesCompleted = 0;

    @Column(name = "carbon_credits", nullable = false)
    @Builder.Default
    private int carbonCredits = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
