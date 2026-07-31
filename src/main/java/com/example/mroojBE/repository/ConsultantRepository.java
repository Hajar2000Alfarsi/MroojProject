package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    Optional<Consultant> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query(value = """
        SELECT c.* FROM consultants c
        WHERE c.specialty_domain = :domain
          AND c.available = TRUE
          AND ST_Distance_Sphere(c.location, ST_SRID(POINT(:lng, :lat), 4326)) <= :radiusMeters
        ORDER BY ST_Distance_Sphere(c.location, ST_SRID(POINT(:lng, :lat), 4326)) ASC,
                 c.current_load ASC
        """, nativeQuery = true)

    List<Consultant> findNearestAvailableByDomain(
            @Param("domain") String domain,
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("radiusMeters") double radiusMeters
    );

    /**
     * Atomic increment — bypasses read-modify-write to avoid the lost-update
     * race when two concurrent bookings match the same consultant. A bulk
     * UPDATE runs entirely in the DB; no in-memory currentLoad value is
     * involved, so there's nothing to race on.
     *
     * CAVEAT: this bypasses the Hibernate persistence context. If the same
     * transaction later reads consultant.getCurrentLoad() on an
     * already-loaded entity, it will see the STALE pre-increment value
     * unless the entity is refreshed. Callers that only need the increment
     * to happen (not to read the new value back) are unaffected.
     */
    @Modifying
    @Query("UPDATE Consultant c SET c.currentLoad = c.currentLoad + 1 WHERE c.id = :id")
    int incrementLoad(@Param("id") Long id);

    /** Same atomicity guarantee as incrementLoad, floored at 0. */
    @Modifying
    @Query("UPDATE Consultant c SET c.currentLoad = CASE WHEN c.currentLoad > 0 THEN c.currentLoad - 1 ELSE 0 END WHERE c.id = :id")
    int decrementLoad(@Param("id") Long id);
}