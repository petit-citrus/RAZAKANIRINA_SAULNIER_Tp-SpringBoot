package com.campus.catalogue.salle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.common.error.BusinessConflictException;

@Service
public class SalleService {

    private final SalleRepository salleRepository;

    public SalleService(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Transactional
    public SalleResponse creer(SalleRequest request) {
        if (salleRepository.existsByNomAndVille(request.nom(), request.ville())) {
            throw new BusinessConflictException("Une salle portant ce nom existe déjà dans cette ville.");
        }

        Salle salle = new Salle(request.nom(), request.ville(), request.capacite());
        return SalleResponse.from(salleRepository.save(salle));
    }
}
