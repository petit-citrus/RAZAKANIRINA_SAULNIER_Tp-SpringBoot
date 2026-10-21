package com.campus.fidelite;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteFideliteRepository
        extends JpaRepository<CarteFidelite, Long> {

    Optional<CarteFidelite> findByClientId(Long clientId);
}