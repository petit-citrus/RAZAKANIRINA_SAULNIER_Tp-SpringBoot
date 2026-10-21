package com.campus.catalogue.concert;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.achat.AchatBilletsRepository;
import com.campus.catalogue.artiste.Artiste;
import com.campus.catalogue.artiste.ArtisteRepository;
import com.campus.catalogue.salle.Salle;
import com.campus.catalogue.salle.SalleRepository;
import com.campus.common.error.ResourceNotFoundException;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ArtisteRepository artisteRepository;
    private final SalleRepository salleRepository;
    private final AchatBilletsRepository achatBilletsRepository;

    public ConcertService(ConcertRepository concertRepository,
                          ArtisteRepository artisteRepository,
                          SalleRepository salleRepository,
                          AchatBilletsRepository achatBilletsRepository) {
        this.concertRepository = concertRepository;
        this.artisteRepository = artisteRepository;
        this.salleRepository = salleRepository;
        this.achatBilletsRepository = achatBilletsRepository;
    }

    @Transactional
    public ConcertResponse creer(ConcertRequest request) {
        Artiste artiste = artisteRepository.findById(request.artisteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artiste introuvable : " + request.artisteId()));
        Salle salle = salleRepository.findById(request.salleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salle introuvable : " + request.salleId()));

        Concert concert = new Concert(
                request.titre(),
                artiste,
                salle,
                request.dateConcert(),
                request.prix());
        return toResponse(concertRepository.save(concert));
    }

    @Transactional(readOnly = true)
    public List<ConcertResponse> rechercher(LocalDateTime du,
                                            LocalDateTime au,
                                            Long artisteId) {
        return concertRepository.rechercher(du, au, artisteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConcertResponse trouverParId(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Concert introuvable : " + id));
        return toResponse(concert);
    }

    private ConcertResponse toResponse(Concert concert) {
        long billetsVendus = achatBilletsRepository
                .sommeQuantiteVendueParConcert(concert.getId());
        return ConcertResponse.from(concert, billetsVendus);
    }
}
