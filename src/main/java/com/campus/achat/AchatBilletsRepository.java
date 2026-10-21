package com.campus.achat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AchatBilletsRepository extends JpaRepository<AchatBillets, Long> {

    @Query("""
            SELECT COALESCE(SUM(a.quantite), 0)
            FROM AchatBillets a
            WHERE a.concert.id = :concertId
            """)
    Long sommeQuantiteVendueParConcert(@Param("concertId") Long concertId);
}
