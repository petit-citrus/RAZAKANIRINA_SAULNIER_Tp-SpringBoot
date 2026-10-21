package com.campus.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;

@Embeddable
public class Adresse {

    @Size(max = 255)
    @Column(name = "rue", length = 255)
    private String rue;

    @Size(max = 255)
    @Column(name = "ville", length = 255)
    private String ville;

    protected Adresse() {
    }

    public Adresse(String rue, String ville) {
        this.rue = rue;
        this.ville = ville;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
}
