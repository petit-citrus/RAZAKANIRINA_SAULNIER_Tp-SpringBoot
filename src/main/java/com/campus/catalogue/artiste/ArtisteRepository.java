package com.campus.catalogue.artiste;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtisteRepository extends JpaRepository<Artiste, Long> {

    boolean existsByNom(String nom);
}
