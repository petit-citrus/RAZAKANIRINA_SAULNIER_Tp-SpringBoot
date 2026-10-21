# Plan d'action — Membre B

## Dépendances avec le périmètre du Membre A

Le travail de ce membre commence après la disponibilité du socle commun du Membre A :

- [ ] `pom.xml` basé sur Spring Boot `4.1.1` et Java 25.
- [ ] MySQL configuré, sans H2.
- [ ] `spring.jpa.hibernate.ddl-auto=validate`.
- [ ] Bean partagé `java.time.Clock`.
- [ ] Migrations Flyway `V1__init_schema.sql` et `V2__seed_data.sql` appliquées.
- [ ] Entités disponibles : `Client`, `Adresse`, `CarteFidelite`, `Concert` et `AchatBillets`.
- [ ] Repositories de base du catalogue et requête de somme des billets vendus disponibles, ou contrat validé avec le Membre A.
- [ ] `@ControllerAdvice` capable de traduire les exceptions métier en `400`, `404` et `409`.
- [ ] Règles Spring Security déjà compatibles avec :
  - [ ] `POST /api/clients` public.
  - [ ] `POST /api/clients/{id}/carte-fidelite` pour `CLIENT` ou `ADMIN`.
  - [ ] `POST /api/achats` pour `CLIENT`.

Le code du Membre B ne doit pas modifier les migrations, les entités ou la configuration de sécurité du Membre A sans accord préalable. Toute adaptation de mapping nécessaire au service d'achat doit être coordonnée avant modification.

## Contrat de compatibilité avec les entités du Membre A

- [ ] Réutiliser l'entité `Client` et son `Adresse @Embeddable`, sans exposer l'entité dans les réponses REST.
- [ ] Réutiliser `CarteFidelite` avec `acheteeLe` et `expireLe`.
- [ ] Réutiliser `Concert`, notamment le prix unitaire et la relation vers `Salle`.
- [ ] Réutiliser `AchatBillets`, notamment le code unique, la quantité, le montant total et `creeLe`.
- [ ] Confirmer le type exact des dates (`LocalDateTime` ou équivalent) avant d'écrire les requêtes.
- [ ] Confirmer la stratégie d'identifiants et les noms de propriétés avant de définir les méthodes de repository.
- [ ] Utiliser uniquement les repositories et services partagés pour rechercher les concerts, les clients et les billets.
- [ ] Conserver les types monétaires en `BigDecimal` avec `DECIMAL(10,2)`.
- [ ] Utiliser des DTO immuables sous forme de records Java pour toutes les entrées et sorties REST.
- [ ] Ne transmettre aucun prix depuis le client HTTP ; le prix doit toujours provenir du `Concert`.

## 1. Préparer l'intégration du périmètre Client

- [ ] Vérifier le package et l'API de l'entité `Client`.
- [ ] Vérifier que l'email est validé par Jakarta Validation et protégé par une contrainte d'unicité en base.
- [ ] Créer un repository client avec recherche par email.
- [ ] Définir une exception métier dédiée pour un email déjà utilisé.
- [ ] Vérifier que cette exception est traduite en `409` par le `@ControllerAdvice` du Membre A.
- [ ] Définir les DTO records :
  - [ ] Requête d'inscription : nom, email, rue, ville.
  - [ ] Réponse de création : identifiant, nom, email et adresse, sans données sensibles ni entité JPA.
- [ ] Implémenter le service d'inscription.
- [ ] Ajouter une vérification applicative de l'email avant insertion.
- [ ] Conserver la contrainte SQL comme protection finale contre les courses concurrentes.
- [ ] Traduire une violation d'unicité éventuelle en conflit métier standardisé, sans exposer le détail SQL.
- [ ] Implémenter `POST /api/clients`.
- [ ] Retourner `201 Created` et un header `Location`.
- [ ] Vérifier que la route reste publique dans la configuration du Membre A.

## 2. Implémenter la carte de fidélité

- [ ] Créer un repository de cartes permettant de rechercher les cartes d'un client.
- [ ] Ajouter une requête permettant d'identifier une carte active à une date donnée :
  - [ ] Carte active si `acheteeLe <= maintenant`.
  - [ ] Carte active si `expireLe > maintenant`.
- [ ] Injecter le bean `Clock` dans le service.
- [ ] Utiliser l'horloge injectée pour obtenir la date d'achat.
- [ ] Calculer l'expiration exactement un an après l'achat avec l'opération de date appropriée.
- [ ] Ne jamais appeler `LocalDateTime.now()` ou `Instant.now()` directement.
- [ ] Vérifier que le client existe ; sinon lever l'exception traduite en `404`.
- [ ] Vérifier qu'aucune carte non expirée n'existe à l'instant courant ; sinon lever un conflit `409`.
- [ ] Définir l'exception métier correspondant à une carte déjà active.
- [ ] Créer le DTO record de réponse de carte.
- [ ] Implémenter `POST /api/clients/{id}/carte-fidelite`.
- [ ] Retourner `201 Created`.
- [ ] Vérifier que le contrôleur ne contient ni logique de date ni logique métier.
- [ ] Vérifier l'autorisation du propriétaire :
  - [ ] Pour un utilisateur `CLIENT`, contrôler que le client ciblé correspond à son identité associée.
  - [ ] Pour `ADMIN`, autoriser l'opération selon la règle du projet.
  - [ ] Si l'association compte/client n'est pas disponible dans le modèle initial, documenter explicitement la séparation retenue et éviter tout contrôle basé uniquement sur une donnée fournie par le client.
- [ ] Ajouter le traitement d'une tentative concurrente de création de carte si nécessaire, en s'appuyant sur les contraintes ou le verrouillage base validé avec le Membre A.

## 3. Définir les calculs financiers

- [ ] Centraliser le calcul du montant d'achat dans le service métier.
- [ ] Calculer le prix brut : `prixUnitaire * quantite`.
- [ ] Appliquer une remise de `10%` si une carte est active à la date de l'achat.
- [ ] Appliquer `0%` en l'absence de carte ou si la carte est expirée.
- [ ] Utiliser `BigDecimal` pour chaque opération financière.
- [ ] Utiliser un arrondi explicite à deux décimales avec `RoundingMode.HALF_UP`.
- [ ] Définir le taux de remise dans le résultat métier afin de pouvoir le retourner dans la réponse.
- [ ] Ne jamais accepter un prix, un taux de remise ou un montant calculé dans la requête HTTP.
- [ ] Tester au minimum le cas `50.00 EUR * 1` :
  - [ ] Sans carte : `50.00`.
  - [ ] Avec carte active : `45.00`.
  - [ ] Avec carte expirée : `50.00`.

## 4. Implémenter l'achat transactionnel

- [ ] Définir le DTO record d'entrée :
  - [ ] `concertId`.
  - [ ] `clientId`.
  - [ ] `quantite`.
- [ ] Annoter `quantite` avec une validation comprise entre `1` et `6`.
- [ ] Définir le DTO record de sortie contenant au minimum :
  - [ ] Code d'achat.
  - [ ] Quantité.
  - [ ] Prix unitaire.
  - [ ] Taux de remise appliqué.
  - [ ] Montant total.
  - [ ] Nombre de places restantes après achat.
- [ ] Créer un service d'achat annoté `@Transactional`.
- [ ] Charger le `Concert` avec un verrou pessimiste d'écriture avant de lire la jauge.
- [ ] Charger le client et vérifier son existence.
- [ ] Calculer la somme des billets déjà vendus après acquisition du verrou.
- [ ] Calculer les places restantes à partir de la capacité de la salle.
- [ ] Refuser l'achat si `placesRestantes < quantite`.
- [ ] Lever une exception métier `JAUGE_INSUFFISANTE`, traduite en `409`.
- [ ] Ne créer aucun `AchatBillets` lorsque la jauge est insuffisante.
- [ ] Déterminer la carte active avec le même `Clock` et dans la même transaction.
- [ ] Générer un code unique, par exemple avec `UUID`.
- [ ] Enregistrer l'achat avec le montant calculé côté serveur.
- [ ] Recalculer les places restantes après insertion avant de construire la réponse.
- [ ] Implémenter `POST /api/achats`.
- [ ] Retourner `201 Created`.
- [ ] Vérifier que tout échec provoque le rollback de la transaction.

## Stratégie de verrouillage en base

Le verrouillage sera porté par la lecture du concert, et non par une synchronisation Java :

- [ ] Définir dans le repository du concert une méthode de recherche par identifiant avec `@Lock(LockModeType.PESSIMISTIC_WRITE)`.
- [ ] Utiliser cette méthode uniquement depuis le service transactionnel d'achat.
- [ ] Vérifier que la transaction est bien ouverte avant cette lecture.
- [ ] Avec MySQL/InnoDB, la lecture verrouillée prend un verrou d'écriture sur la ligne du concert jusqu'au commit ou au rollback.
- [ ] Une seconde transaction visant le même concert attendra la libération du verrou.
- [ ] La seconde transaction relira alors la somme des billets après le commit de la première.
- [ ] Le contrôle de jauge sera donc effectué séquentiellement pour un concert donné.
- [ ] Ne pas utiliser `synchronized`, `ReentrantLock`, variable statique ou cache mémoire pour garantir la capacité.
- [ ] Ne pas faire confiance à un nombre de places calculé avant l'acquisition du verrou.
- [ ] Vérifier que la requête d'agrégation des billets voit les données validées par la transaction précédente.
- [ ] Vérifier que les insertions et le calcul final restent dans la même transaction.
- [ ] Documenter le comportement attendu en cas de timeout ou d'erreur de verrouillage.

Cette stratégie protège également le cas de plusieurs instances de l'application, contrairement à un verrou uniquement en mémoire.

## 5. Implémenter le contrôle d'identité du client acheteur

- [ ] Vérifier le contrat entre l'identifiant HTTP Basic et l'entité `Client`.
- [ ] Ne pas considérer `clientId` comme une preuve suffisante d'identité.
- [ ] Pour un compte `CLIENT`, vérifier que le compte authentifié correspond au client demandé.
- [ ] Pour une séparation explicite du modèle de démonstration, documenter la correspondance entre les comptes de test et les clients seedés.
- [ ] Refuser l'accès à un client tentant d'acheter pour un autre client selon le mécanisme de sécurité retenu.
- [ ] Laisser le rôle `ADMIN` hors de l'endpoint d'achat, conformément aux règles du périmètre.
- [ ] Ajouter des tests de contrôle d'accès si le modèle d'identité le permet.

## 6. Tests unitaires du service d'achat

- [ ] Utiliser une `Clock` fixe injectée dans le service.
- [ ] Utiliser des mocks pour le concert, la salle, le client, la carte et les repositories.
- [ ] Tester un achat sans carte : remise `0%` et montant non remisé.
- [ ] Tester un achat avec carte active : remise `10%` et montant correctement arrondi.
- [ ] Tester un achat avec carte expirée : remise `0%`.
- [ ] Tester une quantité invalide au niveau DTO ou service selon la responsabilité définie.
- [ ] Tester une jauge insuffisante : exception métier `JAUGE_INSUFFISANTE`.
- [ ] Vérifier qu'aucune sauvegarde d'achat n'est effectuée en cas de refus.
- [ ] Tester les montants avec plusieurs quantités et des prix nécessitant un arrondi.
- [ ] Vérifier que les dates sont calculées à partir de la `Clock` fixe.
- [ ] Ne jamais utiliser `Thread.sleep()` dans les tests.

## 7. Tests MVC

- [ ] Tester un achat valide et vérifier le statut `201`.
- [ ] Vérifier la présence des champs minimaux dans la réponse.
- [ ] Vérifier qu'un montant fourni dans la requête est ignoré ou rejeté, puisqu'il ne fait pas partie du DTO accepté.
- [ ] Tester `quantite=0` et vérifier `400`.
- [ ] Tester `quantite=7` et vérifier `400`.
- [ ] Tester une requête anonyme sur `/api/achats` et vérifier `401`.
- [ ] Tester un rôle insuffisant si nécessaire et vérifier `403`.
- [ ] Vérifier que les erreurs utilisent le format du `@ControllerAdvice` du Membre A.
- [ ] Tester l'inscription publique avec données valides.
- [ ] Tester une inscription invalide en `400`.
- [ ] Tester un email déjà utilisé en `409`.
- [ ] Tester la création d'une carte pour un client inexistant en `404`.
- [ ] Tester la création d'une carte déjà active en `409`.

## 8. Test d'intégration MySQL de la transaction

- [ ] Utiliser Testcontainers MySQL avec `@ServiceConnection`.
- [ ] Démarrer l'application avec Flyway et `ddl-auto=validate`.
- [ ] Préparer un concert possédant exactement cinq places restantes.
- [ ] Vider ou isoler les achats du concert avant le scénario.
- [ ] Utiliser deux appels au service transactionnel ou deux appels HTTP contrôlés par le test.
- [ ] Utiliser un `ExecutorService` avec deux tâches.
- [ ] Utiliser un `CountDownLatch` pour libérer les deux tâches au même moment.
- [ ] Ne pas utiliser `Thread.sleep()` pour provoquer la concurrence.
- [ ] Faire demander quatre billets à chaque tâche.
- [ ] Attendre explicitement la fin des deux tâches et récupérer leurs résultats.
- [ ] Vérifier qu'une seule transaction réussit avec un résultat équivalent à `201`.
- [ ] Vérifier que l'autre transaction lève l'exception `JAUGE_INSUFFISANTE`, équivalente à `409`.
- [ ] Vérifier qu'il n'existe qu'un seul achat de quatre billets après le scénario.
- [ ] Vérifier qu'il reste exactement une place en base.
- [ ] Vérifier qu'aucune transaction n'a créé d'enregistrement partiel.
- [ ] Répéter le scénario suffisamment pour détecter une implémentation reposant par erreur sur un verrou mémoire.
- [ ] Nettoyer les ressources de l'`ExecutorService` avec une fermeture contrôlée.

## 9. Rédiger le `README.md`

- [ ] Décrire le projet et la séparation des responsabilités entre les deux membres.
- [ ] Documenter les prérequis :
  - [ ] JDK 25.
  - [ ] Maven ou Maven Wrapper.
  - [ ] Docker fonctionnel pour MySQL/Testcontainers.
- [ ] Documenter le démarrage de MySQL.
- [ ] Documenter les variables de connexion nécessaires.
- [ ] Documenter le lancement de l'application avec `java -jar`.
- [ ] Documenter les commandes Maven, notamment `./mvnw clean verify`.
- [ ] Décrire les trois comptes HTTP Basic `CLIENT`, `PRODUCTEUR` et `ADMIN` fournis par le Membre A.
- [ ] Documenter les mots de passe de démonstration uniquement s'ils sont explicitement prévus pour un environnement local et ne sont pas des secrets réels.
- [ ] Lister les endpoints publics et protégés.
- [ ] Documenter l'inscription client.
- [ ] Documenter la création et l'expiration des cartes de fidélité.
- [ ] Documenter la remise de `10%` et la condition d'activité de la carte.
- [ ] Documenter le format de la requête d'achat.
- [ ] Préciser que le prix est calculé côté serveur.
- [ ] Documenter les réponses `201`, `400`, `401`, `403`, `404` et `409`.
- [ ] Expliquer la stratégie de concurrence : transaction Spring, verrou pessimiste MySQL/InnoDB sur la ligne du concert et recalcul de la jauge après verrouillage.
- [ ] Indiquer que les verrous en mémoire sont volontairement interdits pour permettre le fonctionnement multi-instance.
- [ ] Décrire l'exécution des tests Testcontainers et le besoin de Docker.

## 10. Validation finale et partage

- [ ] Vérifier que tous les tests du Membre A passent sans modification fonctionnelle.
- [ ] Exécuter `mvn clean verify`.
- [ ] Vérifier que les tests utilisent MySQL/Testcontainers et jamais H2.
- [ ] Vérifier qu'aucun appel direct à `LocalDateTime.now()`, `Instant.now()` ou `Thread.sleep()` n'est présent dans le périmètre.
- [ ] Vérifier que tous les calculs monétaires utilisent `BigDecimal` et `RoundingMode.HALF_UP`.
- [ ] Vérifier que les contrôleurs ne calculent ni prix ni jauge.
- [ ] Vérifier l'absence d'entités JPA dans les réponses REST.
- [ ] Vérifier le format uniforme des erreurs fourni par le `@ControllerAdvice`.
- [ ] Vérifier que le test de concurrence est stable et laisse une place restante.
- [ ] Vérifier que le README correspond aux commandes et comptes réellement configurés.
- [ ] Partager la branche ou le commit après validation complète avec le Membre A.
