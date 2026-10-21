
package com.campus.achat.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.achat.AchatBillets;
import com.campus.achat.AchatBilletsRepository;
import com.campus.achat.dto.AchatRequest;
import com.campus.achat.dto.AchatResponse;
import com.campus.catalogue.concert.Concert;
import com.campus.catalogue.concert.ConcertRepository;
import com.campus.client.Client;
import com.campus.client.ClientRepository;
import com.campus.common.error.BusinessConflictException;
import com.campus.common.error.ResourceNotFoundException;
import com.campus.fidelite.CarteFidelite;
import com.campus.fidelite.CarteFideliteRepository;

@Service
public class AchatService {

    private final AchatBilletsRepository achatBilletsRepository;
    private final ConcertRepository concertRepository;
    private final ClientRepository clientRepository;
    private final CarteFideliteRepository carteFideliteRepository;
    private final CalculMontantService calculMontantService;
    private final Clock clock;

    public AchatService(
            AchatBilletsRepository achatBilletsRepository,
            ConcertRepository concertRepository,
            ClientRepository clientRepository,
            CarteFideliteRepository carteFideliteRepository,
            CalculMontantService calculMontantService,
            Clock clock) {

        this.achatBilletsRepository = achatBilletsRepository;
        this.concertRepository = concertRepository;
        this.clientRepository = clientRepository;
        this.carteFideliteRepository = carteFideliteRepository;
        this.calculMontantService = calculMontantService;
        this.clock = clock;
    }

    @Transactional
    public AchatResponse creer(
            AchatRequest request,
            Authentication authentication) {

        // 1. Vérifier les données de la requête
        if (request == null
                || request.concertId() == null
                || request.clientId() == null
                || request.concertId() < 1
                || request.clientId() < 1
                || request.quantite() == null
                || request.quantite() < 1
                || request.quantite() > 6) {
            throw new IllegalArgumentException("Requête d'achat invalide.");
        }

        // 2. Vérifier l'authentification
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentification requise.");
        }

        // 3. Retrouver le client
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client introuvable."));

        // 4. Vérifier que le client achète pour lui-même
        String emailAuthentifie = authentication.getName()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!client.getEmail().equalsIgnoreCase(emailAuthentifie)) {
            throw new AccessDeniedException(
                    "Vous ne pouvez pas acheter pour un autre client.");
        }

        // 5. Verrouiller le concert dans la transaction
        Concert concert = concertRepository
                .findByIdForUpdate(request.concertId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Concert introuvable."));

        LocalDateTime maintenant = LocalDateTime.now(clock);

        // 6. Vérifier que le concert est dans le futur
        if (!concert.getDateConcert().isAfter(maintenant)) {
            throw new BusinessConflictException(
                    "Le concert n'est plus ouvert à la réservation.");
        }

        // 7. Calculer les places disponibles sous le verrou
        long quantiteVendue = achatBilletsRepository
                .sommeQuantiteVendueParConcert(concert.getId());

        long placesDisponibles =
                concert.getSalle().getCapacite() - quantiteVendue;

        if (placesDisponibles < request.quantite()) {
            throw new BusinessConflictException(
                    "Le concert ne dispose pas de suffisamment de places disponibles.");
        }

        // 8. Vérifier si la carte de fidélité est active
        CarteFidelite carte = carteFideliteRepository
                .findByClientId(client.getId())
                .orElse(null);

        boolean carteActive = carte != null
                && !carte.getDateAchat().isAfter(maintenant)
                && carte.getDateExpiration().isAfter(maintenant);

        // 9. Calculer le montant total
        BigDecimal prixUnitaire = concert.getPrix();

        BigDecimal montantTotal = calculMontantService.calculerMontant(
                prixUnitaire,
                request.quantite(),
                carteActive
        );

        BigDecimal tauxRemise = carteActive
                ? new BigDecimal("0.10")
                : BigDecimal.ZERO;

        // 10. Créer et enregistrer l'achat
        String codeAchat = UUID.randomUUID().toString();

        AchatBillets achat = new AchatBillets(
                codeAchat,
                concert,
                client,
                request.quantite(),
                montantTotal,
                maintenant
        );

        achatBilletsRepository.save(achat);

        // 11. Retourner la réponse
        int placesRestantesApresAchat =
                (int) (placesDisponibles - request.quantite());

        return new AchatResponse(
                codeAchat,
                request.quantite(),
                prixUnitaire,
                tauxRemise,
                montantTotal,
                placesRestantesApresAchat
        );
    }
}
