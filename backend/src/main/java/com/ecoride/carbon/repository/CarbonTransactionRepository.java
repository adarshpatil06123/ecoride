package com.ecoride.carbon.repository;

import com.ecoride.carbon.entity.CarbonTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CarbonTransactionRepository extends JpaRepository<CarbonTransaction, UUID> {

    List<CarbonTransaction> findByUserId(UUID userId);

    boolean existsByUserIdAndRideId(UUID userId, UUID rideId);

    @Query("SELECT COALESCE(SUM(c.carbonSavedGrams), 0) FROM CarbonTransaction c")
    long sumAllCarbonSaved();

    @Query("SELECT COALESCE(SUM(c.carbonSavedGrams), 0) FROM CarbonTransaction c WHERE c.user.id = :userId")
    long sumCarbonSavedByUser(@Param("userId") UUID userId);
}
