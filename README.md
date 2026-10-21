# Examen de TP Billetterie de concerts professionnelle

**Membres du binôme :**
- RAZAKANIRINA Aingo (Membre B)
- SAULNIER Simon (Membre A)

__Lien vers le projet :__ https://github.com/petit-citrus/RAZAKANIRINA_SAULNIER_Tp-SpringBoot.git

---

## Introduction et objectifs
L'objectif de ce projet est de développer une plateforme professionnelle de billetterie de concerts. Le système doit permettre de gérer un catalogue (artistes, salles, concerts), d'inscrire des clients, de gérer un programme de fidélité et de vendre des billets.
L'enjeu principal réside dans la robustesse technique : livrer une API REST sécurisée, résistante aux accès concurrents (pour ne jamais dépasser la jauge des salles), entièrement testée, et respectant des standards de qualité stricts (build reproductible via Maven, absence totale de dépendance à la base de données en mémoire H2, etc.).

## Analyse des besoins et spécifications
Le projet repose sur plusieurs règles métier et contraintes techniques incontournables :
- **Modèle de données :** 6 entités principales (Artiste, Salle, Concert, Client, CarteFidelite, AchatBillets) avec des contraintes d'unicité (ex: email client, couple nom/ville pour une salle).
- **Règles de gestion :** Calcul de remises côté serveur (10% avec une carte de fidélité active d'un an exact), gestion monétaire via `BigDecimal` et dates en UTC.
- **Concurrence :** Empêcher la survente lors d'achats simultanés (jauge).
- **Contrat HTTP :** Format d'erreur JSON standardisé et strict pour toutes les exceptions (400, 401, 403, 404, 409).
- **Sécurité :** Contrôle d'accès basé sur les rôles (CLIENT, PRODUCTEUR, ADMIN) via HTTP Basic.

## Architecture et choix techniques
Nous avons opté pour un socle technologique moderne et robuste, conforme aux exigences de l'énoncé :
- **Langage & Framework :** Java 25 et Spring Boot 4.1.1.
- **Base de données :** MySQL exclusivement.
- **Migrations :** Flyway pour versionner le schéma (`V1` pour la structure, `V2` pour le jeu de données), avec Hibernate strictement configuré en `ddl-auto=validate`.
- **Architecture logicielle :** Architecture en couches (N-Tiers) avec une séparation stricte entre les Entités JPA, les Services (logique transactionnelle) et les Contrôleurs REST.
- **Échanges de données :** Utilisation exclusive de `records` Java (DTO) pour éviter toute fuite du modèle interne. Lombok a été volontairement exclu pour éviter les conflits de compilation avec Java 25.
- **Tests :** JUnit 5, MockMvc pour la couche web, et Testcontainers pour les tests d'intégration avec de vrais conteneurs MySQL.

---

## Utilisation de l'intelligence artificielle

### Interaction n°1 : Prompt de division
Notre premier prompt visait à séparer le projet en deux périmètres distincts sans chevauchement pour éviter les conflits Git. L'IA a scindé le travail en une "Phase 0 / Membre A" (Socle, Maven, Flyway, Sécurité, Modèle complet, Catalogue) et une "Phase 2 / Membre B" (Clients, Fidélité, Moteur transactionnel d'achat et Concurrence).

---

## Démarche de développement

### Partie 1 : Socle, Catalogue et Sécurité (exécutée par Simon - Membre A)

Pour ma partie, j'ai adopté une démarche de "Prompt Engineering" structurée. Plutôt que de demander à l'IA de coder directement le projet, j'ai agi en lui demandant un processus de développement itératif et documenté. Mes consignes pour l'agent IA ont suivi ces 4 grandes étapes :

1. **Définir le plan d'action :** Avant d'écrire la moindre ligne de code, j'ai fourni le contexte complet et les contraintes à l'IA pour qu'elle génère un plan d'action détaillé sous forme de "To-Do list" dans un fichier nommé `MembreA.md`.
2. **Vérifier ce dernier :** J'ai ensuite demandé à l'IA de faire une analyse critique (Sanity Check) de son propre plan pour identifier à l'avance les pièges liés à Java 25, la configuration de Flyway vs Hibernate, et les problématiques de Testcontainers.
3. **Développer en notant les problèmes dans le suivi `MembreA.md` :** J'ai ordonné à l'IA de générer le code étape par étape (Pom.xml, Entités, Repositories, Sécurité...). À chaque itération, l'IA devait cocher les cases de son plan d'action et documenter obligatoirement tout obstacle technique ou choix d'implémentation dans une section "⚠️ Problèmes rencontrés" à la fin du fichier de suivi.
4. **Faire les tests puis vérifier que bien tous les problèmes ont été résolus :** Une fois le code métier produit, j'ai instruit l'IA de rédiger les tests (JPA, WebMvc, Integration Testcontainers). Après l'exécution de la commande `mvn clean verify` avec Docker activé, nous avons vérifié les logs pour nous assurer de la résolution effective de tous les points en suspens dans le document de suivi, avant de valider et de pousser le socle commun pour le Membre B.

---

### Partie 2 : Clients, Fidélité et Achats (exécutée par Aingo - Membre B)

Prompt :
> "Tu es un développeur Java senior spécialisé en Spring Boot, Spring Data JPA et architecture logicielle.
> Contexte : Nous travaillons en binôme sur un projet de billetterie de concerts avec Java 25, Spring Boot 4.1.1, Maven, MySQL, Flyway et Spring Security.
> Je suis le Membre B et mes responsabilités sont décrites dans le fichier MembreB.md. Le Membre A travaille sur les responsabilités décrites dans MembreA.md.
> Avant de générer du code, analyse les fichiers existants du dépôt, notamment pom.xml, MembreA.md et MembreB.md.
> Je souhaite obtenir :
> 1. Un état des lieux du projet actuel.
> 2. Les fonctionnalités qui relèvent exactement de mon périmètre Membre B.
> 3. Les dépendances de mon travail envers celui du Membre A.
> 4. Les classes, packages et DTO que je devrai créer.
> 5. Les éléments que je peux préparer immédiatement.
> 6. Les éléments que je ne dois pas développer avant que le socle commun soit disponible.
> 7. Les risques de conflits Git ou de duplication de code.
>
> Contraintes : Respecter l'architecture en couches prévue. Utiliser des DTO Java records. Ne jamais exposer les entités JPA directement dans les réponses REST. Ne pas modifier les fichiers du Membre A. Ne pas inventer des entités ou méthodes déjà prévues mais encore absentes. Identifier explicitement les informations manquantes.
> Pour le moment, ne crée et ne modifie aucun fichier. Je veux uniquement une analyse structurée et des recommandations justifiées."

### Interaction IA : Calcul financier des billets et intégration
- **Objectif :** implémenter un calcul de prix fiable, indépendant de la base de données, et l'intégrer au flux d'achat.
- **Utilisation de GitHub Copilot :** génération d'une classe Java utilisant `BigDecimal` et de tests JUnit 5. L'IA a été utilisée comme un assistant de proposition et d'analyse, sans délégation aveugle.
- **Analyse critique :** la première proposition couvrait les scénarios principaux, mais pas suffisamment les valeurs limites et les arrondis. De plus, les propositions initiales de Copilot pour les services renvoyaient parfois des entités JPA ou omettaient des vérifications (ex: date de début de la carte de fidélité).
- **Amélioration :** ajout de tests pour les quantités autorisées, les entrées invalides et la précision financière (12 tests unitaires réussis). Correction manuelle des propositions de l'IA pour forcer l'utilisation stricte des DTO et compléter la logique de validité.
- **Décision d'architecture :** séparer le calcul du montant de la vérification de validité de la carte de fidélité. Verrouillage pessimiste de la base de données géré dans une transaction unique lors de l'achat.

---

### Partie 3 : Fusion et tests d'intégration complets (partie commune)

- **Objectif :** intégrer les fonctionnalités développées par les deux membres dans un seul et même projet, puis exécuter les tests d'intégration pour valider le comportement global du système.
- **Méthodologie :** utilisation de Git pour fusionner les branches de développement, gestion fine des historiques (stash) et résolution des conflits d'intégration.
- **Résultat attendu :** un backend cohérent, fonctionnel et prêt à être évalué hors de l'IDE via une construction Maven reproductible.

Pour consolider le projet de manière sécurisée, un agent IA a été utilisé en tant qu'auditeur. Il a été chargé de croiser et d'analyser les plans d'action respectifs des deux membres (`MembreA.md` et `MembreB.md`) afin d'identifier les points de liaison critiques (comme l'interception des nouvelles exceptions métier par le `ControllerAdvice` partagé ou la déclaration correcte des beans Spring). Une fois cet audit réalisé, une vérification manuelle a été effectuée, les erreurs de configuration ont été corrigées, et l'intégralité de la suite de tests d'intégration (incluant le test de concurrence strict sur MySQL via Testcontainers) a été exécutée pour le contrôle final.

--- 

## Difficultés rencontrées et solutions

### Problème 1 : Erreur de compilation Java 25 (invalid target release: 25)
**Explication et solution :**  
L'utilisation historique des balises `<maven.compiler.source>` et `<maven.compiler.target>` entraînait des conflits lors de la compilation avec le JDK 25. La solution a été d'utiliser la propriété `<maven.compiler.release>25</maven.compiler.release>`, qui garantit la cohérence stricte de l'API Java utilisée lors de la phase de `build`.

### Problème 2 : Conflit de cache avec Testcontainers
**Explication et solution :**
Lors de l'exécution des tests d'intégration, Spring tentait de réutiliser le contexte de l'application en cache, mais le conteneur MySQL avait été éteint entre deux tests, causant des erreurs de connexion. Nous avons résolu cela en utilisant l'annotation `@DirtiesContext` pour forcer le rechargement du contexte Spring.

### Problème 3 : Incompatibilité de dépendances avec Spring Boot 4.1.1
**Explication et solution :**
Les gestionnaires d'erreurs de Spring Security utilisaient l'ancienne version de Jackson. Nous avons dû adapter les imports pour utiliser Jackson 3 (`tools.jackson.databind.ObjectMapper`) requis par Spring Boot 4.

### Problème 4 : Intégration Git du socle commun
**Explication et solution :**
Le Membre B avait commencé à travailler sur la logique de calcul de prix de manière isolée avant la disponibilité du socle. Pour fusionner le travail du Membre A sans perdre le code local, nous avons utilisé `git stash` pour mettre à l'abri les modifications, fusionné la branche `feat/socle-et-catalogue`, puis restauré le stash de manière ciblée afin d'éviter les conflits sur les fichiers de configuration de l'IDE.

---

## Résultats obtenus
Le backend de billetterie est pleinement fonctionnel et valide le contrat exigé. Le build `mvn clean verify` s'exécute avec succès avec Java 25.
- Les migrations Flyway configurent la base MySQL et Hibernate valide le schéma (`ddl-auto=validate`).
- Les tests unitaires (notamment les 12 tests sur le calcul financier) garantissent l'exactitude des remises (10% avec carte de fidélité active) et des arrondis.
- Les tests d'intégration avec Testcontainers assurent que les opérations métier, les accès concurrents (bloqués correctement grâce au verrouillage pessimiste) et les sécurités (HTTP Basic) sont respectés.

## Bilan et conclusion
Ce projet a mis en évidence l'importance d'une conception rigoureuse (contrats d'interfaces, DTOs, gestion centralisée des erreurs) et d'une organisation Git propre en équipe. La séparation stricte du travail via des contrats inter-domaines a permis d'éviter les conflits majeurs, le Membre A fournissant le socle et le Membre B construisant la logique d'achat par-dessus. L'utilisation de l'IA comme un outil (et non un dirigeant) s'est révélée être nécessaire pour la quantité de travail et le peu de temps disponible.
## Comment on est arrivé au résultat :
L'architecture a été conçue pour être professionnelle et robuste. Nous avons appliqué le modèle MVC/N-Tiers de manière stricte :
- **Contrôleurs :** Ne contiennent aucune logique métier. Ils ne font que réceptionner les requêtes HTTP, valider les DTO d'entrée, appeler les services et retourner les DTO de sortie avec les bons codes HTTP.
- **Services :** Concentrent toute la logique métier, délimitent les transactions (`@Transactional`), gèrent les verrous de base de données et manipulent les données.
- **Repositories :** Gèrent l'accès exclusif aux données via Spring Data JPA.
  De plus, la centralisation des erreurs via un `@RestControllerAdvice` assure un contrat API (JSON) uniforme pour les clients de l'API.

## Modèle IA : Copilot Github ?
L'usage de GitHub Copilot a été piloté par des prompts directifs. Au lieu de demander du code brut, nous avons forcé l'IA à adopter un rôle d'assistant soumis à des contraintes fortes ("Aucun Lombok", "H2 interdit", "Format JSON strict", "Ne pas exposer les entités JPA"). Les erreurs générées par l'IA (comme l'oubli des contraintes Jackson 3 ou des lacunes dans la logique métier) ont été corrigées grâce à une phase de compilation systématique, à l'analyse manuelle des logs Maven et à la relecture critique du code proposé.