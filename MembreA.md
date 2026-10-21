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

## Garde-fous obligatoires — vulnérabilités et solutions préventives

### Étape 1 — Socle Maven, Java 25 et absence de Lombok

- **Vulnérabilité — compilation Java incohérente** : `source` et `target` seuls ne garantissent pas une API Java cohérente et peuvent laisser diverger le JDK local et celui de la CI.
  - **Solution** : remplacer ces propriétés par `<maven.compiler.release>25</maven.compiler.release>`, imposer un JDK 25 réel et vérifier que le Maven Compiler Plugin résolu supporte Java 25.
- **Vulnérabilité — versions incompatibles** : redéfinir les versions gérées par Spring Boot peut produire un ensemble Spring incohérent.
  - **Solution** : utiliser le parent Spring Boot 4.1.1, laisser le parent/BOM gérer les versions et contrôler la résolution avec `mvn dependency:tree`.
- **Vulnérabilité — incompatibilité Springdoc** : `springdoc-openapi-starter-webmvc-ui:3.1.1` peut ne pas être compatible avec Spring Boot 4.1.1.
  - **Solution** : vérifier sa matrice de compatibilité avant intégration et confirmer le démarrage effectif de l'application.
- **Vulnérabilité — contrat JPA incomplet sans Lombok** : l'absence de génération automatique peut provoquer des erreurs de persistance ou des entités exposées directement.
  - **Solution** : écrire explicitement les constructeurs, accesseurs et méthodes métier ; fournir un constructeur sans argument compatible JPA, éviter `toString` sur les relations et ne jamais exposer une entité comme DTO.

### Étape 2 — MySQL, Flyway et Hibernate

- **Vulnérabilité — schéma non maîtrisé** : `ddl-auto=create/update` ou H2 masquerait les divergences entre le développement et MySQL.
  - **Solution** : MySQL uniquement, aucun artefact H2, Flyway comme seul outil de création/modification et `spring.jpa.hibernate.ddl-auto=validate`.
- **Vulnérabilité — écart Flyway/JPA** : avec `validate`, toute différence de nom, type, nullabilité ou relation fait échouer le démarrage.
  - **Solution** : déclarer explicitement `@Table`, `@Column` et `@JoinColumn`, puis tester sur une base MySQL vide l'ordre Flyway puis validation Hibernate.
- **Vulnérabilité — modèle mal dénombré** : le modèle contient six entités JPA et `Adresse`, qui est un objet de valeur.
  - **Solution** : créer exactement six `@Entity` (`Artiste`, `Salle`, `Concert`, `Client`, `CarteFidelite`, `AchatBillets`) et `Adresse` en `@Embeddable`, sans table dédiée.
- **Vulnérabilité — types SQL implicites** : les conversions automatiques peuvent diverger pour les prix, dates, enums, longueurs et colonnes obligatoires.
  - **Solution** : aligner explicitement `BigDecimal` sur `DECIMAL(10,2)`, `LocalDateTime` sur `DATETIME`, les longueurs, enums, `NOT NULL`, index et clés étrangères dans les annotations et `V1__init_schema.sql`.
- **Vulnérabilité — concurrence sur les contraintes métier** : un contrôle préalable en Java ne protège pas deux requêtes simultanées.
  - **Solution** : conserver les contraintes `UNIQUE`, `CHECK` pertinentes et clés étrangères en base, appliquer une stratégie transactionnelle et convertir les violations d'intégrité en `409`.
- **Vulnérabilité — dates dépendantes de la machine** : une date future peut devenir passée selon le fuseau ou l'horloge.
  - **Solution** : utiliser UTC partout, injecter un `Clock` et une horloge fixe dans les tests ; la règle de date future reste vérifiée dans le service.

### Étape 3 — Données initiales et erreurs

- **Vulnérabilité — seed non reproductible** : des identifiants implicites, dates passées ou insertions dans le mauvais ordre rendent `V2__seed_data.sql` dépendante de l'état de la base.
  - **Solution** : valeurs déterministes, ordre des clés étrangères respecté, dates futures contrôlées et exécution de `V1` puis `V2` sur une base MySQL vide.
- **Vulnérabilité — fuite d'informations techniques** : une exception SQL, une stacktrace ou un message Hibernate renvoyé au client révèle la structure interne.
  - **Solution** : mapper explicitement les erreurs vers le DTO JSON strict, journaliser les détails uniquement côté serveur et ne jamais les inclure dans la réponse.
- **Vulnérabilité — erreurs de sécurité hors contrat JSON** : `401` et `403` ne passent pas automatiquement par le `@RestControllerAdvice`.
  - **Solution** : configurer un `AuthenticationEntryPoint` et un `AccessDeniedHandler` produisant exactement le même JSON d'erreur.

### Étape 4 — Spring Security

- **Vulnérabilité — confusion rôle/autorité** : `hasRole("ADMIN")` et `hasAuthority("ROLE_ADMIN")` n'ont pas le même contrat et peuvent autoriser la mauvaise route.
  - **Solution** : adopter une convention unique `ROLE_CLIENT`, `ROLE_PRODUCTEUR`, `ROLE_ADMIN` et tester chaque route en anonyme, rôle insuffisant et rôle autorisé.
- **Vulnérabilité — matcher trop large ou mal ordonné** : un pattern générique placé avant une règle spécifique peut rendre une opération protégée publique.
  - **Solution** : placer les règles spécifiques avant les patterns génériques, distinguer les méthodes HTTP et tester toutes les routes publiques/protégées.
- **Vulnérabilité — mots de passe exposés** : des comptes de démonstration en clair dans le code ou la configuration peuvent être réutilisés.
  - **Solution** : injecter les identifiants par variables d'environnement/profil de test et ne conserver que des valeurs BCrypt.

### Étape 5 — Testcontainers MySQL et Maven/CI

- **Vulnérabilité — Docker indisponible** : Testcontainers échoue si Docker Desktop ou le daemon CI n'est pas accessible.
  - **Solution** : documenter Docker comme prérequis, utiliser un runner CI compatible Docker, vérifier le démarrage d'un conteneur avant `mvn test` et ne prévoir aucun fallback H2.
- **Vulnérabilité — datasource hors conteneur** : l'application peut utiliser une URL MySQL locale au lieu des propriétés du conteneur.
  - **Solution** : utiliser `@ServiceConnection` si supporté par Spring Boot 4.1.1, sinon `@DynamicPropertySource`, mais jamais les deux pour la même datasource.
- **Vulnérabilité — tests dépendants de la machine** : ports fixes, démarrages manuels par test ou mode Testcontainers `reuse` créent des courses et des échecs non reproductibles.
  - **Solution** : fixer la même image MySQL en local et en CI, utiliser des ports aléatoires, un cycle de vie JUnit Jupiter partagé et ne pas dépendre de `reuse`.
- **Vulnérabilité — tests JUnit ignorés** : une mauvaise configuration Surefire peut ne pas détecter les tests JUnit 5.
  - **Solution** : vérifier la détection avec `mvn test`, laisser le parent gérer Surefire lorsque possible et réserver Failsafe aux tests réellement séparés.
- [ ] Ajouter un test de démarrage sur MySQL vide vérifiant explicitement Flyway puis `ddl-auto=validate`.
- [ ] Vérifier que la même image MySQL et les mêmes versions Maven sont utilisées en local et en CI.
- [ ] Vérifier que Docker est disponible avant l'exécution de la suite.

### Étape 6 — Validation avant partage du socle

- **Vulnérabilité — partage prématuré** : une divergence de schéma, de sécurité ou de format d'erreur découverte après le travail en parallèle coûte cher à corriger.
  - **Solution** : ne partager le socle qu'après `mvn clean test` complet sur Testcontainers MySQL, validation des réponses `400/401/403/404/409`, contrôle des secrets et confirmation JDK 25.
- [ ] Vérifier que les six entités et `Adresse @Embeddable` correspondent exactement à Flyway.
- [ ] Vérifier que les handlers MVC et Security produisent le même format JSON strict.
- [ ] Vérifier que le build utilise `maven.compiler.release=25`.
