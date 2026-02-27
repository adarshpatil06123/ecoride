package com.ecoride.trust.repository;

import com.ecoride.trust.entity.TrustConnection;
import com.ecoride.trust.entity.TrustConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrustConnectionRepository extends JpaRepository<TrustConnection, TrustConnectionId> {

    /**
     * Find trust connection regardless of canonical ordering.
     */
    @Query("""
            SELECT t FROM TrustConnection t
            WHERE (t.user1Id = :a AND t.user2Id = :b)
               OR (t.user1Id = :b AND t.user2Id = :a)
            """)
    Optional<TrustConnection> findBetween(@Param("a") UUID a, @Param("b") UUID b);

    @Query("""
            SELECT t FROM TrustConnection t
            WHERE t.user1Id = :userId OR t.user2Id = :userId
            """)
    List<TrustConnection> findAllForUser(@Param("userId") UUID userId);
}
