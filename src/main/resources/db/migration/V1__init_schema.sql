CREATE TABLE artiste (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    genre_musical VARCHAR(255),
    CONSTRAINT pk_artiste PRIMARY KEY (id),
    CONSTRAINT uk_artiste_nom UNIQUE (nom)
);

CREATE TABLE salle (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    ville VARCHAR(255) NOT NULL,
    capacite INT NOT NULL,
    CONSTRAINT pk_salle PRIMARY KEY (id),
    CONSTRAINT uk_salle_nom_ville UNIQUE (nom, ville),
    CONSTRAINT ck_salle_capacite CHECK (capacite BETWEEN 50 AND 50000)
);

CREATE TABLE concert (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    artiste_id BIGINT NOT NULL,
    salle_id BIGINT NOT NULL,
    date_concert DATETIME NOT NULL,
    prix DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_concert PRIMARY KEY (id),
    CONSTRAINT ck_concert_prix CHECK (prix > 0),
    CONSTRAINT fk_concert_artiste FOREIGN KEY (artiste_id) REFERENCES artiste (id),
    CONSTRAINT fk_concert_salle FOREIGN KEY (salle_id) REFERENCES salle (id)
);

CREATE TABLE client (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    rue VARCHAR(255),
    ville VARCHAR(255),
    CONSTRAINT pk_client PRIMARY KEY (id),
    CONSTRAINT uk_client_email UNIQUE (email)
);

CREATE TABLE carte_fidelite (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    date_achat DATETIME NOT NULL,
    date_expiration DATETIME NOT NULL,
    CONSTRAINT pk_carte_fidelite PRIMARY KEY (id),
    CONSTRAINT uk_carte_fidelite_client UNIQUE (client_id),
    CONSTRAINT fk_carte_fidelite_client FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE TABLE achat_billets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL,
    concert_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    quantite INT NOT NULL,
    montant_total DECIMAL(10, 2) NOT NULL,
    date_creation DATETIME NOT NULL,
    CONSTRAINT pk_achat_billets PRIMARY KEY (id),
    CONSTRAINT uk_achat_billets_code UNIQUE (code),
    CONSTRAINT ck_achat_billets_quantite CHECK (quantite BETWEEN 1 AND 6),
    CONSTRAINT ck_achat_billets_montant CHECK (montant_total > 0),
    CONSTRAINT fk_achat_billets_concert FOREIGN KEY (concert_id) REFERENCES concert (id),
    CONSTRAINT fk_achat_billets_client FOREIGN KEY (client_id) REFERENCES client (id)
);
