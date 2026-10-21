package com.campus.catalogue.artiste;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.common.error.BusinessConflictException;

@Service
public class ArtisteService {

    private final ArtisteRepository artisteRepository;

    public ArtisteService(ArtisteRepository artisteRepository) {
        this.artisteRepository = artisteRepository;
    }

    @Transactional
    public ArtisteResponse creer(ArtisteRequest request) {
        if (artisteRepository.existsByNom(request.nom())) {
            throw new BusinessConflictException("Un artiste portant ce nom existe déjà.");
        }

        Artiste artiste = new Artiste(request.nom(), request.genreMusical());
        return ArtisteResponse.from(artisteRepository.save(artiste));
    }
}
