# Plan d'action — Membre A

## Analyse du dépôt actuel

Le dépôt est actuellement un squelette Java :

- `pom.xml` ne contient ni parent Maven Spring Boot ni dépendance.
- La propriété Java utilise encore `maven.compiler.source` et `maven.compiler.target` ; elle devra être remplacée par `maven.compiler.release`.
- La seule classe présente est `com.campus.Main`, générée comme programme Java simple.
- Aucun dossier de ressources, modèle JPA, repository, service, contrôleur, migration Flyway ou test n'existe.
- Aucun commit Git n'existe encore.

## Étapes d'implémentation

### 1. Mettre en place le socle Maven Spring Boot

- [ ] Remplacer le `pom.xml` minimal par un projet basé sur `spring-boot-starter-parent` en version `4.1.1`.
- [ ] Définir `<maven.compiler.release>25</maven.compiler.release>`.
- [ ] Ne pas redéfinir les versions gérées par Spring Boot.
- [ ] Définir uniquement la version `3.1.1` pour `springdoc-openapi-starter-webmvc-ui`.
- [ ] Ajouter les dépendances applicatives :
  - [ ] `spring-boot-starter-webmvc`.
  - [ ] `spring-boot-starter-data-jpa`.
  - [ ] `spring-boot-starter-validation`.
  - [ ] `spring-boot-starter-security`.
  - [ ] `spring-boot-starter-flyway`.
  - [ ] `flyway-mysql` en runtime.
  - [ ] `mysql-connector-j` en runtime.
  - [ ] `spring-boot-starter-actuator`.
  - [ ] `springdoc-openapi-starter-webmvc-ui:3.1.1`.
- [ ] Ajouter les dépendances de test :
  - [ ] `spring-boot-starter-test`.
  - [ ] `spring-boot-starter-webmvc-test`.
  - [ ] `spring-boot-starter-data-jpa-test`.
  - [ ] `spring-boot-starter-security-test`.
  - [ ] `spring-boot-testcontainers`.
  - [ ] `testcontainers-mysql`.
  - [ ] `testcontainers-junit-jupiter`.
- [ ] Remplacer `Main` par une classe principale Spring Boot avec `main(String[] args)`.
- [ ] Vérifier le socle avec `mvn validate`, puis `mvn test`.

### 2. Créer la configuration applicative de base

- [ ] Ajouter `src/main/resources/application.yml` ou `application.properties`.
- [ ] Configurer MySQL comme seule base de données.
- [ ] Configurer la datasource par variables d'environnement.
- [ ] Configurer `spring.jpa.hibernate.ddl-auto=validate`.
- [ ] Activer Flyway au démarrage.
- [ ] Configurer les dates en UTC.
- [ ] Fournir un bean `java.time.Clock`, par défaut avec `Clock.systemUTC()`.
- [ ] Prévoir une horloge fixe injectable dans les tests.
- [ ] Ne jamais ajouter H2.

### 3. Définir la structure de packages

- [ ] Organiser le code par responsabilité et domaine :
  - [ ] `config`.
  - [ ] `security`.
  - [ ] `common/error`.
  - [ ] `catalogue/artiste`.
  - [ ] `catalogue/salle`.
  - [ ] `catalogue/concert`.
  - [ ] `client`.
  - [ ] `fidelite`.
  - [ ] `achat`.
- [ ] Séparer entités JPA, DTO, repositories, services et contrôleurs.
- [ ] Utiliser des records Java pour tous les DTO REST entrants et sortants.
- [ ] Ne jamais exposer directement une entité JPA.
- [ ] Ne pas utiliser Lombok.

### 4. Construire le modèle JPA et la migration initiale

- [ ] Créer `src/main/resources/db/migration/V1__init_schema.sql`.
- [ ] Créer les tables `artiste`, `salle`, `concert`, `client`, `carte_fidelite` et `achat_billets`.
- [ ] Définir les clés primaires et étrangères.
- [ ] Définir les contraintes d'unicité :
  - [ ] Nom de l'artiste.
  - [ ] Email du client.
  - [ ] Code d'achat.
  - [ ] Couple nom/ville d'une salle.
- [ ] Utiliser `DECIMAL(10,2)` pour les prix et montants.
- [ ] Utiliser des types SQL compatibles avec `LocalDateTime`.
- [ ] Ajouter les contraintes de capacité, quantité et prix.
- [ ] Créer les entités JPA :
  - [ ] `Artiste` : id, nom obligatoire et unique, genre musical.
  - [ ] `Salle` : id, nom, ville, capacité de 50 à 50 000.
  - [ ] `Concert` : id, titre, artiste obligatoire, salle obligatoire, date future, prix positif.
  - [ ] `Client` : id, nom, email unique, adresse embarquée.
  - [ ] `Adresse` : objet `@Embeddable` avec rue et ville.
  - [ ] `CarteFidelite` : id, client, dates d'achat et d'expiration.
  - [ ] `AchatBillets` : id, code unique, concert, client, quantité de 1 à 6, montant total, date de création.
- [ ] Mapper les associations `ManyToOne` obligatoires.
- [ ] Ajouter les validations Jakarta correspondantes.
- [ ] Vérifier que Hibernate accepte le schéma avec `ddl-auto=validate`.

## Socle commun partageable

Le **socle commun Entités + Flyway est terminé à la fin de l'étape 4**, lorsque les conditions suivantes sont remplies :

- [ ] Les six entités JPA et `Adresse` compilent.
- [ ] `V1__init_schema.sql` s'exécute sur MySQL.
- [ ] Hibernate démarre avec `ddl-auto=validate`.
- [ ] Les relations, clés étrangères, index et contraintes d'unicité sont validés.
- [ ] Un test d'intégration Testcontainers MySQL confirme l'application de Flyway.
- [ ] `mvn test` passe.

À partir de ce point, le socle peut être partagé avec le binôme. Les contrôleurs et services pourront être développés en parallèle, sans modifier la structure fondamentale des entités sans coordination.

### 5. Ajouter les données initiales

- [ ] Créer `V2__seed_data.sql`.
- [ ] Insérer deux artistes.
- [ ] Insérer deux salles.
- [ ] Insérer trois concerts.
- [ ] Insérer deux clients.
- [ ] Utiliser des dates de concerts futures.
- [ ] Respecter l'ordre des clés étrangères.
- [ ] Vérifier l'exécution complète des migrations sur une base MySQL vide.

### 6. Mettre en place les repositories

- [ ] Créer les repositories Spring Data JPA.
- [ ] Ajouter la recherche des concerts par date minimale `du`.
- [ ] Ajouter la recherche par date maximale `au`.
- [ ] Ajouter la recherche par artiste.
- [ ] Définir le comportement lorsque les filtres sont absents.
- [ ] Ajouter une requête d'agrégation pour la somme des quantités achetées par concert.
- [ ] Prévoir le calcul des places restantes à partir de la capacité et des billets vendus.
- [ ] Tester ces requêtes avec Testcontainers MySQL.

### 7. Implémenter le domaine Catalogue et Production

- [ ] Créer les DTO records d'artistes.
- [ ] Créer le service de création d'artiste.
- [ ] Détecter les doublons d'artistes.
- [ ] Créer les DTO records de salles.
- [ ] Créer le service de création de salle.
- [ ] Contrôler l'unicité du couple nom/ville.
- [ ] Créer les DTO records de concerts.
- [ ] Créer le service de création de concert.
- [ ] Vérifier l'existence de l'artiste et de la salle.
- [ ] Vérifier la date future et le prix positif.
- [ ] Implémenter les endpoints :
  - [ ] `GET /api/concerts?du=&au=&artiste=`.
  - [ ] `GET /api/concerts/{id}`.
  - [ ] `POST /api/production/artistes`.
  - [ ] `POST /api/production/concerts`.
  - [ ] `POST /api/admin/salles`.
- [ ] Retourner `201 Created` avec un en-tête `Location` pour les créations.
- [ ] Retourner le nombre de places restantes dans le détail d'un concert.
- [ ] Garder les contrôleurs fins et déléguer la logique aux services.

### 8. Implémenter la gestion globale des erreurs

- [ ] Créer un DTO record d'erreur contenant exactement :
  - [ ] `timestamp`.
  - [ ] `status`.
  - [ ] `code`.
  - [ ] `message`.
  - [ ] `path`.
- [ ] Créer un `@RestControllerAdvice`.
- [ ] Retourner `400` pour les erreurs de validation.
- [ ] Retourner `404` pour les ressources inexistantes.
- [ ] Retourner `409` pour les doublons, conflits métier et problèmes de jauge.
- [ ] Utiliser le `Clock` configuré pour produire le timestamp.
- [ ] Utiliser le chemin réel de la requête.
- [ ] Ne jamais exposer de stacktrace, de détail SQL ou de message technique.
- [ ] Ajouter des tests MVC sur la structure JSON exacte.

### 9. Configurer Spring Security

- [ ] Configurer l'authentification HTTP Basic.
- [ ] Déclarer un `BCryptPasswordEncoder`.
- [ ] Déclarer trois comptes de démonstration : `CLIENT`, `PRODUCTEUR` et `ADMIN`.
- [ ] Autoriser publiquement :
  - [ ] `GET /api/concerts/**`.
  - [ ] `POST /api/clients`.
  - [ ] `/actuator/health`.
  - [ ] `/v3/api-docs/**`.
  - [ ] `/swagger-ui/**`.
- [ ] Autoriser `PRODUCTEUR` ou `ADMIN` sur :
  - [ ] `POST /api/production/artistes`.
  - [ ] `POST /api/production/concerts`.
- [ ] Autoriser `ADMIN` sur `POST /api/admin/salles`.
- [ ] Autoriser `CLIENT` ou `ADMIN` sur `POST /api/clients/{id}/carte-fidelite`.
- [ ] Autoriser `CLIENT` sur `POST /api/achats`.
- [ ] Vérifier `401` pour un anonyme sur route protégée.
- [ ] Vérifier `403` lorsqu'un rôle authentifié est insuffisant.

### 10. Configurer Actuator et OpenAPI

- [ ] Exposer publiquement uniquement `/actuator/health`.
- [ ] Ne pas exposer les autres endpoints Actuator.
- [ ] Autoriser publiquement `/v3/api-docs`, `/v3/api-docs/**`, `/swagger-ui.html` et `/swagger-ui/**`.
- [ ] Ajouter les métadonnées OpenAPI de base.
- [ ] Vérifier la documentation des DTO records.
- [ ] Vérifier que Swagger respecte les règles de sécurité.

### 11. Ajouter les tests du périmètre

- [ ] Tests JPA :
  - [ ] Recherche filtrée des concerts.
  - [ ] Filtre par artiste.
  - [ ] Filtre par intervalle de dates.
  - [ ] Somme des billets vendus.
  - [ ] Calcul des places restantes.
- [ ] Tests MVC :
  - [ ] Création d'artiste valide.
  - [ ] Validation invalide en `400`.
  - [ ] Doublon en `409`.
  - [ ] Concert inexistant en `404`.
  - [ ] Salle dupliquée en `409`.
- [ ] Tests de sécurité :
  - [ ] Anonyme sur route protégée en `401`.
  - [ ] `CLIENT` sur création de salle en `403`.
  - [ ] `PRODUCTEUR` autorisé à créer un artiste et un concert.
  - [ ] `ADMIN` autorisé à créer une salle.
  - [ ] `CLIENT` autorisé à créer un achat.
- [ ] Tests d'intégration Testcontainers MySQL :
  - [ ] Démarrage complet de l'application.
  - [ ] Application de `V1__init_schema.sql`.
  - [ ] Application de `V2__seed_data.sql`.
  - [ ] Contraintes d'unicité.
  - [ ] Contraintes de clés étrangères.
  - [ ] Validation de `ddl-auto=validate`.
- [ ] Vérifier qu'aucun test n'utilise H2.

### 12. Validation finale avant partage

- [ ] Exécuter `mvn clean test`.
- [ ] Vérifier le démarrage avec MySQL/Testcontainers.
- [ ] Vérifier les endpoints avec les rôles prévus.
- [ ] Vérifier qu'aucun contrôleur n'expose une entité JPA.
- [ ] Vérifier le format des erreurs `400`, `404` et `409`.
- [ ] Vérifier les endpoints Actuator et OpenAPI.
- [ ] Vérifier l'absence de secrets et de fichiers temporaires.
- [ ] Partager le socle avec le binôme après validation de l'étape 4.
- [ ] Partager la version fonctionnelle complète après validation des étapes 7 à 11.
