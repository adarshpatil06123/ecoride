package com.ecoride.ride.repository;

import com.ecoride.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {

    /**
     * Core matching engine query — filter by zone, time window, and OPEN status.
     * Application layer computes additional score weighting on top.
     */
    @Query("SELECT r FROM Ride r " +
           "WHERE r.pickupZone = :zone " +
           "AND r.departureTime BETWEEN :from AND :to " +
           "AND r.status = :status " +
           "ORDER BY r.departureTime ASC")
    List<Ride> findMatchingRides(@Param("zone") String zone,
                                  @Param("from") Instant from,
                                  @Param("to") Instant to,
                                  @Param("status") Ride.Status status);

    List<Ride> findByDriver_Id(UUID driverId);
}
