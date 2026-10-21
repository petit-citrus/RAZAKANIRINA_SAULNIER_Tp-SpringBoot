INSERT INTO artiste (id, nom, genre_musical)
VALUES
    (1, 'Les Astres', 'Rock alternatif'),
    (2, 'Lumiere Nocturne', 'Electro pop');

INSERT INTO salle (id, nom, ville, capacite)
VALUES
    (1, 'Le Zenith', 'Paris', 5000),
    (2, 'La Manufacture', 'Lyon', 1200);

INSERT INTO concert (id, titre, artiste_id, salle_id, date_concert, prix)
VALUES
    (1, 'Les Astres en concert', 1, 1, '2027-03-15 20:00:00', 45.00),
    (2, 'Lumiere Nocturne live', 2, 2, '2027-04-10 20:30:00', 32.50),
    (3, 'Festival des Astres', 1, 2, '2027-05-22 19:00:00', 55.00);

INSERT INTO client (id, nom, email, rue, ville)
VALUES
    (1, 'Alice Martin', 'alice.martin@example.test', '1 rue des Lilas', 'Paris'),
    (2, 'Bob Durant', 'bob.durant@example.test', '12 avenue Victor Hugo', 'Lyon');
