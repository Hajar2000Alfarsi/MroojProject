package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    Optional<Consultant> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    /**
     * Core query for the Phase 4 assignment engine.
     * Finds AVAILABLE consultants matching the requested domain, ordered by
     * geographic distance (meters, via ST_Distance_Sphere) then by lowest
     * current_load — the two signals the ERD explicitly calls out
     * ("specialization and location") plus load-balancing as a tiebreak.
     *
     * :lng / :lat are the booking's location coordinates (WGS84).
     * :radiusMeters caps the search (e.g. 100000 = 100km) so a consultant
     * on the far side of Oman never gets matched over an unavailable
     * nearby one falling back to "anyone in the country."
     */
    @Query(value = """
            SELECT c.* FROM consultants c
            WHERE c.specialty_domain = :domain
              AND c.available = TRUE
              AND ST_Distance_Sphere(c.location, POINT(:lng, :lat)) <= :radiusMeters
            ORDER BY ST_Distance_Sphere(c.location, POINT(:lng, :lat)) ASC,
                     c.current_load ASC
            """, nativeQuery = true)
    List<Consultant> findNearestAvailableByDomain(
            @Param("domain") String domain,
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("radiusMeters") double radiusMeters
    );
}