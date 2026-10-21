package com.campus.catalogue.concert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query("""
            SELECT c
            FROM Concert c
            WHERE (:du IS NULL OR c.dateConcert >= :du)
              AND (:au IS NULL OR c.dateConcert <= :au)
              AND (:artisteId IS NULL OR c.artiste.id = :artisteId)
            ORDER BY c.dateConcert ASC
            """)
    List<Concert> rechercher(@Param("du") LocalDateTime du,
                             @Param("au") LocalDateTime au,
                             @Param("artisteId") Long artisteId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Concert c WHERE c.id = :id")
    Optional<Concert> findByIdForUpdate(@Param("id") Long id);
}
