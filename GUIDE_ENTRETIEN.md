# Guide de préparation à l'entretien technique

## 1. Présentation rapide du projet

Ce projet répond à deux besoins métier :

1. permettre à un client de consulter l'historique de ses commandes ;
2. permettre à un client de demander le remboursement d'un produit précis en fournissant une description du problème et une photo justificative.

Le backend est développé avec Java, Spring Boot, Spring Web, Spring Data JPA et H2.

Une phrase simple pour présenter le projet pendant l'entretien :

> J'ai construit une API REST qui permet de consulter les commandes d'un client et de créer une demande de remboursement pour une ligne de commande. J'ai isolé les règles métier de Spring et de la base de données en utilisant une Clean Architecture pragmatique.

---

## 2. Contraintes du sujet

Les contraintes techniques explicites étaient :

- Java 25 ;
- Spring Boot ;
- Clean Architecture ;
- Vue.js pour le frontend ;
- un fichier `README.md` ;
- démontrer que le MVP fonctionne ;
- inclure de la logique métier, et pas uniquement transférer les données entre le frontend et le backend ;
- prendre soi-même les décisions fonctionnelles lorsque les critères d'acceptation ne sont pas précisés ;
- réaliser l'exercice dans un temps limité, recommandé à environ deux heures.

La principale difficulté fonctionnelle est que le sujet ne donne pas de critères d'acceptation précis pour un remboursement.

J'ai donc défini des règles cohérentes et je les ai documentées explicitement comme des hypothèses.

---

## 3. Hypothèses et règles métier choisies

Les règles retenues sont :

- une commande doit appartenir au client qui effectue la demande ;
- le produit doit appartenir à la commande sélectionnée ;
- seule une commande livrée peut être remboursée ;
- une demande doit être effectuée dans les 30 jours suivant la livraison ;
- une seule demande de remboursement est autorisée par ligne de commande ;
- la description est obligatoire ;
- la description contient entre 10 et 500 caractères ;
- la photo justificative est obligatoire ;
- les formats acceptés sont JPEG, PNG et WebP ;
- la taille maximale de la preuve est de 5 Mo ;
- une demande acceptée reçoit le statut `SUBMITTED`.

### Pourquoi ces règles ?

Le sujet demande de la logique métier mais ne définit pas les conditions d'un remboursement. Ces règles permettent de créer un parcours réaliste et testable sans inventer un système excessivement complexe.

La durée de 30 jours est une décision fonctionnelle. Elle ne vient pas explicitement du sujet. Il faut le dire clairement pendant l'entretien :

> Le sujet ne précisait pas la durée d'éligibilité. J'ai choisi 30 jours comme hypothèse métier raisonnable et je l'ai documentée. Dans un vrai projet, cette règle serait validée avec le Product Owner.

---

## 4. Architecture choisie

Le backend utilise une Clean Architecture pragmatique.

```text
domain
├── model
└── exception

application
├── port
│   ├── in
│   └── out
└── service

infrastructure
├── config
├── persistence
└── web
```

### Sens des dépendances

```text
Web / JPA → Application → Domain
```

Le domaine ne dépend ni de Spring, ni de JPA, ni de H2.

Les services applicatifs dépendent d'interfaces, appelées ports. Les adaptateurs techniques implémentent ces ports.

### Pourquoi cette architecture ?

J'ai choisi cette architecture pour :

- isoler les règles métier des détails techniques ;
- tester les règles sans démarrer Spring ou une base de données ;
- pouvoir remplacer H2 par PostgreSQL sans modifier les services métier ;
- éviter de placer toute la logique dans les contrôleurs ;
- rendre les responsabilités de chaque classe explicites.

### Pourquoi une approche pragmatique ?

Pour un kata de deux heures, créer de nombreux modules Maven aurait ajouté beaucoup de configuration. J'ai donc conservé un seul module Maven avec des packages clairement séparés.

Cela respecte l'objectif de séparation des responsabilités tout en restant proportionné à la taille de l'exercice.

---

## 5. Couche domaine

Le domaine contient les modèles métier indépendants de Spring.

### `OrderStatus`

Cette énumération représente les états possibles d'une commande :

```java
public enum OrderStatus {
    PROCESSING,
    SHIPPED,
    DELIVERED
}
```

Pourquoi une énumération ?

- elle empêche les valeurs invalides ;
- elle est plus sûre qu'une chaîne de caractères ;
- elle rend les règles comme `status == DELIVERED` explicites.

### `OrderItem`

Cette classe représente une ligne de commande :

- identifiant de la ligne ;
- nom du produit ;
- quantité ;
- prix unitaire.

Elle calcule également son prix total :

```java
public BigDecimal totalPrice() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
}
```

Pourquoi `BigDecimal` ?

`double` peut produire des erreurs d'arrondi avec les montants monétaires. `BigDecimal` donne un comportement décimal précis.

### `CustomerOrder`

Cette classe représente une commande client.

Elle contient :

- l'identifiant de la commande ;
- l'identifiant du client ;
- la date de commande ;
- la date de livraison ;
- le statut ;
- les lignes de commande.

Elle contient aussi des comportements métier simples :

- chercher une ligne par son identifiant ;
- calculer le total de la commande.

Pourquoi copier la liste avec `List.copyOf` ?

Cela évite qu'un appelant modifie le contenu d'une commande après sa création.

### `RefundRequest`

Cette classe représente une demande de remboursement :

- identifiant ;
- commande concernée ;
- ligne concernée ;
- description ;
- métadonnées de la preuve ;
- statut ;
- date de création.

La preuve contient :

- le nom du fichier ;
- le type MIME ;
- la taille.

Le fichier binaire n'est pas stocké dans la base H2.

Pourquoi ?

Dans un système réel, les images seraient plutôt placées dans un stockage objet comme S3, Azure Blob Storage ou MinIO. La base conserverait uniquement une référence vers le fichier.

---

## 6. Ports entrants

Les ports entrants représentent les fonctionnalités proposées par l'application.

### `GetOrderHistoryUseCase`

Ce port définit le cas d'usage permettant de récupérer l'historique d'un client.

```java
List<CustomerOrder> getForCustomer(String customerId);
```

### `SubmitRefundUseCase`

Ce port définit la création d'une demande de remboursement.

Il reçoit un `SubmitRefundCommand`.

Pourquoi utiliser une commande ?

- éviter une méthode contenant trop de paramètres indépendants ;
- regrouper les données nécessaires au cas d'usage ;
- ne pas faire dépendre le service métier de `MultipartFile`, qui appartient à Spring Web ;
- rendre le cas d'usage appelable depuis une API REST, un batch ou un autre adaptateur.

---

## 7. Ports sortants

Les ports sortants représentent les services dont l'application a besoin.

### `OrderRepository`

Il permet :

- de rechercher les commandes d'un client ;
- de rechercher une commande précise appartenant à un client.

La méthode combine `orderId` et `customerId` afin de vérifier l'appartenance au niveau de la requête.

### `RefundRepository`

Il permet :

- de vérifier si une demande existe déjà pour une ligne ;
- d'enregistrer une demande.

Pourquoi les interfaces se trouvent-elles dans `application` ?

Le cas d'usage définit ce dont il a besoin. La couche technique fournit ensuite une implémentation. Ainsi, l'application ne dépend pas de Spring Data JPA.

---

## 8. Services applicatifs

### `OrderService`

Le service récupère les commandes puis les trie de la plus récente à la plus ancienne.

Le tri se trouve dans le service pour rendre le comportement du cas d'usage explicite.

Dans une application avec beaucoup de données et de pagination, ce tri devrait être réalisé directement dans la requête SQL.

### `RefundService`

Cette classe contient l'essentiel de la logique métier :

1. validation de la description ;
2. validation du format et de la taille de la preuve ;
3. vérification de l'existence et de l'appartenance de la commande ;
4. vérification du statut `DELIVERED` ;
5. vérification du délai de 30 jours ;
6. vérification que le produit appartient à la commande ;
7. vérification de l'absence de demande existante ;
8. création de la demande ;
9. sauvegarde via le port `RefundRepository`.

Pourquoi injecter `Clock` ?

Appeler directement `LocalDate.now()` ou `Instant.now()` rend les tests dépendants de la date réelle.

Avec un `Clock` injecté, le test peut fixer la date :

```java
Clock.fixed(instant, ZoneOffset.UTC)
```

Cela rend les tests déterministes.

Pourquoi générer l'identifiant avec `UUID` ?

Une demande de remboursement a besoin d'un identifiant unique. Pour ce kata, un UUID évite de dépendre de la stratégie d'identifiant de la base.

---

## 9. Persistance JPA et H2

### Pourquoi H2 ?

H2 a été retenue parce que :

- elle ne nécessite pas l'installation d'un serveur de base de données ;
- elle démarre avec l'application ;
- elle convient à une démonstration et à un test technique ;
- elle permet de montrer une vraie persistance avec JPA ;
- elle fournit une console web.

La base est configurée en mode fichier :

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/refunddb
```

Pourquoi le mode fichier et pas seulement le mode mémoire ?

Le mode fichier conserve les demandes de remboursement après le redémarrage de l'application. Le mode mémoire aurait supprimé toutes les données à chaque arrêt.

### Entités JPA

Les entités techniques sont séparées des modèles métier :

- `OrderEntity` ;
- `OrderItemEntity` ;
- `RefundEntity`.

Pourquoi ne pas annoter directement les modèles métier avec `@Entity` ?

Cela aurait couplé le domaine à JPA. La séparation permet de faire évoluer le modèle de stockage indépendamment du modèle métier.

Le coût de ce choix est la présence de méthodes de mapping `fromDomain` et `toDomain`.

### Relation commande/lignes

Une commande possède plusieurs lignes :

```java
@OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.EAGER
)
```

Pourquoi `cascade = ALL` ?

Les lignes font partie de l'agrégat commande. Sauvegarder une commande doit également sauvegarder ses lignes.

Pourquoi `orphanRemoval = true` ?

Si une ligne est retirée de la commande persistée, elle ne doit pas rester seule dans la base.

Pourquoi `EAGER` ?

L'API retourne toujours les lignes avec la commande et `open-in-view` est désactivé.

Pour un volume important, je remplacerais cette approche par une requête avec `join fetch`, une projection ou un `EntityGraph` afin de mieux contrôler les chargements.

### Repositories Spring Data

Les interfaces Spring Data étendent `JpaRepository`.

Elles sont internes à l'infrastructure et ne sont pas utilisées directement par les services métier.

### Adaptateurs JPA

`JpaOrderRepository` implémente le port `OrderRepository`.

`JpaRefundRepository` implémente le port `RefundRepository`.

Leur responsabilité est :

- appeler Spring Data ;
- convertir les entités JPA en modèles métier ;
- convertir les modèles métier en entités JPA.

---

## 10. Initialisation des données

`DemoDataInitializer` insère quatre commandes au démarrage uniquement lorsque la table des commandes est vide.

```java
if (orders.count() > 0) {
    return;
}
```

Pourquoi ?

Le projet doit être démontrable immédiatement, sans demander à l'évaluateur d'insérer manuellement des données.

Dans un environnement de production, ces données ne seraient pas créées par l'application. Elles pourraient être limitées à un profil `demo` ou `dev`.

---

## 11. Couche Web

### `OrderController`

Le contrôleur expose :

```http
GET /api/orders
```

Il lit l'identifiant du client depuis l'en-tête `X-Customer-Id`.

Pourquoi cet en-tête ?

Le sujet ne demande pas de système d'authentification. Cet en-tête simule temporairement l'utilisateur connecté.

Dans un vrai système, l'identifiant proviendrait du token JWT ou du contexte Spring Security. Le client ne devrait pas pouvoir choisir librement cet identifiant.

### `RefundController`

Le contrôleur expose :

```http
POST /api/refunds
Content-Type: multipart/form-data
```

Les champs sont :

- `orderId` ;
- `itemId` ;
- `description` ;
- `evidence`.

Pourquoi `multipart/form-data` ?

La requête contient à la fois du texte et un fichier.

Le contrôleur transforme `MultipartFile` en données simples avant d'appeler le cas d'usage. Le service métier ne dépend donc pas de Spring Web.

### `ApiExceptionHandler`

Cette classe transforme les exceptions en réponses HTTP cohérentes.

Une règle métier non respectée renvoie :

```http
422 Unprocessable Entity
```

Pourquoi 422 ?

La requête est syntaxiquement valide, mais elle ne peut pas être traitée à cause d'une règle métier, par exemple une commande trop ancienne.

Les erreurs suivent le format Problem Details afin de fournir un titre, un statut et un détail lisibles par le frontend.

---

## 12. Configuration Spring

`ApplicationConfiguration` crée les services applicatifs comme beans Spring.

Pourquoi ne pas annoter directement les services avec `@Service` ?

Le but est de garder les services applicatifs indépendants de Spring. La configuration se charge de l'assemblage des dépendances.

Cela rend l'orientation des dépendances plus visible.

---

## 13. Tests

### Tests unitaires de `RefundService`

Les tests utilisent de faux repositories et un `Clock` fixe.

Ils vérifient notamment :

- la création d'une demande valide ;
- le rejet d'un produit absent de la commande ;
- le rejet d'une demande en doublon ;
- le rejet d'un format de preuve non autorisé.

Pourquoi des faux objets plutôt que Mockito partout ?

De petits fakes rendent le scénario lisible et évitent de coupler le test à des détails d'interaction. Ils stockent uniquement les informations nécessaires au test.

### Test d'intégration H2

Le test `H2PersistenceTest` démarre le contexte Spring avec une base H2 en mémoire.

Il vérifie :

- la création du schéma JPA ;
- la détection des repositories ;
- l'exécution de l'initialisation ;
- la lecture des commandes et de leurs lignes.

Pourquoi utiliser une base en mémoire dans le test ?

Chaque exécution part d'une base propre et ne modifie pas la base locale du développeur.

---

## 14. Contraintes rencontrées et solutions

### Critères d'acceptation absents

**Contrainte :** le sujet ne précisait pas les règles d'éligibilité.

**Solution :** définir des hypothèses simples, cohérentes, testables et documentées.

### Temps limité

**Contrainte :** la durée recommandée est d'environ deux heures.

**Solution :** limiter le MVP à deux endpoints, éviter une authentification complète et commencer avec une architecture simple mais extensible.

### Respect de la Clean Architecture

**Contrainte :** Spring et JPA peuvent facilement se retrouver dans toutes les classes.

**Solution :** conserver le domaine sans annotations techniques et utiliser des ports et adaptateurs.

### Upload d'une image

**Contrainte :** stocker correctement un fichier demande un stockage dédié et des contrôles de sécurité.

**Solution du kata :** valider le type déclaré et la taille, puis stocker uniquement les métadonnées.

**Amélioration production :** vérifier la signature réelle du fichier, lancer une analyse antivirus et sauvegarder le contenu dans un stockage objet.

### Java 25 non installé localement au départ

**Contrainte :** la machine de développement utilisait Java 21.

**Solution :** conserver Java 25 comme cible du projet et utiliser temporairement une surcharge Maven avec Java 21 pour les vérifications locales, puis installer JDK 25 pour respecter la livraison.

### Maven non installé globalement

**Contrainte :** la commande `mvn` n'était pas disponible.

**Solution :** ajouter Maven Wrapper afin que le projet télécharge et utilise automatiquement la bonne version de Maven.

---

## 15. Limites connues

Le projet est un MVP. Les limites sont :

- pas d'authentification réelle ;
- identité client simulée par un en-tête ;
- pas de pagination de l'historique ;
- pas de stockage réel des images ;
- pas d'analyse antivirus ;
- pas de workflow de traitement des remboursements ;
- pas de migration Flyway ;
- contrôle du type de fichier basé sur le type MIME déclaré ;
- H2 n'est pas la base recommandée pour la production ;
- gestion simplifiée de la concurrence sur les doublons.

Il est important de présenter ces limites spontanément. Cela montre que les compromis sont conscients.

---

## 16. Améliorations possibles

Si davantage de temps était disponible :

1. ajouter Spring Security et une authentification JWT ;
2. remplacer H2 par PostgreSQL ;
3. gérer le schéma avec Flyway ;
4. stocker les images dans S3 ou MinIO ;
5. ajouter une contrainte unique SQL sur `(order_id, item_id)` dans les remboursements ;
6. ajouter la pagination de l'historique ;
7. créer des statuts `UNDER_REVIEW`, `APPROVED` et `REJECTED` ;
8. ajouter un endpoint de suivi des demandes ;
9. ajouter des tests d'API avec MockMvc ;
10. utiliser Testcontainers pour tester PostgreSQL ;
11. ajouter OpenAPI/Swagger ;
12. ajouter des logs structurés et des métriques.

---

## 17. Questions probables pendant l'entretien

### Pourquoi ne pas utiliser directement `JpaRepository` dans le service ?

Parce que cela ferait dépendre la logique applicative de Spring Data. Le port permet de remplacer la persistance et de tester le service sans base.

### Pourquoi séparer entités JPA et objets métier ?

Pour éviter que les contraintes de stockage dictent la forme du domaine. Cela ajoute du mapping, mais protège le cœur métier.

### Pourquoi H2 ?

Pour avoir une démonstration autonome et rapide. Pour la production, je choisirais PostgreSQL.

### Pourquoi utiliser `BigDecimal` ?

Pour éviter les erreurs d'arrondi des nombres flottants lors des calculs monétaires.

### Pourquoi injecter `Clock` ?

Pour rendre les règles liées au temps déterministes et facilement testables.

### Pourquoi renvoyer 422 ?

Parce que la requête est bien formée, mais rejetée par une règle métier.

### Pourquoi ne pas stocker l'image dans H2 ?

Une image est mieux adaptée à un stockage objet. La base doit conserver sa référence et ses métadonnées.

### Comment éviter deux demandes simultanées pour le même produit ?

La vérification applicative actuelle améliore le message utilisateur, mais ne suffit pas contre une course concurrente. J'ajouterais une contrainte unique en base sur `(order_id, item_id)` et je convertirais l'exception SQL en erreur métier.

### Comment remplacer H2 par PostgreSQL ?

Il faudrait :

- remplacer la dépendance H2 par le driver PostgreSQL ;
- changer l'URL et les identifiants de connexion ;
- introduire des migrations Flyway ;
- conserver les mêmes ports et services.

Les règles métier ne changeraient pas.

### Pourquoi ne pas faire plusieurs modules Maven ?

Le projet est petit et limité dans le temps. Les packages suffisent à matérialiser les frontières. Sur une application plus grande, des modules Maven pourraient empêcher les dépendances interdites au moment de la compilation.

---

## 18. Démonstration conseillée

Pendant la démonstration :

1. démarrer le backend ;
2. afficher `GET /api/orders` ;
3. montrer une commande livrée et une commande expédiée ;
4. soumettre une demande valide ;
5. montrer la réponse `201 Created` ;
6. ouvrir la console H2 et afficher `REFUND_REQUESTS` ;
7. renvoyer la même demande pour montrer le rejet du doublon ;
8. présenter rapidement le test de `RefundService` ;
9. expliquer le sens des dépendances de l'architecture.

Requête SQL utile dans la console H2 :

```sql
SELECT * FROM CUSTOMER_ORDERS;
SELECT * FROM ORDER_ITEMS;
SELECT * FROM REFUND_REQUESTS;
```

---

## 19. Résumé oral en une minute

> J'ai commencé par analyser les deux cas d'usage et, comme les critères d'acceptation n'étaient pas fournis, j'ai défini et documenté des règles simples : commande livrée, délai de 30 jours, produit appartenant à la commande, description et photo obligatoires, et absence de doublon. J'ai utilisé une Clean Architecture pragmatique pour isoler le domaine et les services de Spring et de JPA. Les services dépendent de ports, implémentés par des adaptateurs Spring Data JPA sur H2. H2 fonctionne en mode fichier pour conserver les demandes après redémarrage. Les contrôleurs restent fins et délèguent la logique aux cas d'usage. J'ai injecté une horloge pour tester les règles temporelles et ajouté des tests unitaires ainsi qu'un test d'intégration H2. Les principales limites sont l'absence d'authentification réelle et de stockage objet pour les images, que j'ajouterais dans une version de production.

---

## 20. Point important pour votre nouveau projet

Dans le projet existant, le package racine est différent. Dans votre nouveau projet, utilisez :

```java
package com.kata.carrefour;
```

Tous les sous-packages doivent commencer par :

```text
com.kata.carrefour.domain
com.kata.carrefour.application
com.kata.carrefour.infrastructure
```

Votre classe principale doit rester au niveau le plus haut :

```text
com.kata.carrefour.CarrefourApplication
```

Spring Boot pourra ainsi détecter automatiquement les composants placés dans tous les sous-packages.
