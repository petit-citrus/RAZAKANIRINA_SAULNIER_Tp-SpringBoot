package com.campus.catalogue.concert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.campus.catalogue.artiste.Artiste;
import com.campus.catalogue.salle.Salle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "concert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artiste_id", nullable = false)
    private Artiste artiste;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @NotNull
    @Column(name = "date_concert", nullable = false)
    private LocalDateTime dateConcert;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "prix", nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    protected Concert() {
    }

    public Concert(String titre, Artiste artiste, Salle salle,
                   LocalDateTime dateConcert, BigDecimal prix) {
        this.titre = titre;
        this.artiste = artiste;
        this.salle = salle;
        this.dateConcert = dateConcert;
        this.prix = prix;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Artiste getArtiste() {
        return artiste;
    }

    public void setArtiste(Artiste artiste) {
        this.artiste = artiste;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public LocalDateTime getDateConcert() {
        return dateConcert;
    }

    public void setDateConcert(LocalDateTime dateConcert) {
        this.dateConcert = dateConcert;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
}
