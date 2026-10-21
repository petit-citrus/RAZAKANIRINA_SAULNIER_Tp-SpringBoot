package com.campus.catalogue.salle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, Long> {

    boolean existsByNomAndVille(String nom, String ville);
}
