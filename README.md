# Carrefour Java Kata — historique et remboursements

Ce projet couvre le MVP demandé : un client consulte l'historique de ses commandes et peut demander le remboursement d'un produit en joignant une description et une photo.

## Démarrage

Prérequis : JDK 25, Maven 3.6.3+ et Node.js 20+.

Dans deux terminaux :

```bash
cd backend
mvn spring-boot:run
```

```bash
cd frontend
npm install
npm run dev
```

L'interface est disponible sur `http://localhost:5173`. Vite relaie les appels `/api` vers le backend sur `http://localhost:8080`.

Tests et build :

```bash
cd backend && mvn test
cd frontend && npm run build
```

## Critères d'acceptation retenus

Le sujet laisse volontairement les critères ouverts. Les décisions suivantes ont été prises :

- l'historique est trié de la commande la plus récente à la plus ancienne ;
- seul un produit d'une commande livrée peut faire l'objet d'une demande ;
- la demande est acceptée jusqu'à 30 jours après la livraison ;
- une seule demande est possible par ligne de commande ;
- la description contient entre 10 et 500 caractères ;
- la preuve est obligatoire, au format JPEG, PNG ou WebP, et ne dépasse pas 5 Mo ;
- une demande acceptée prend immédiatement le statut `SUBMITTED`.

Quatre commandes de démonstration sont associées au client `customer-1`. L'identité est simulée par l'en-tête `X-Customer-Id`, avec cette valeur par défaut.

## Architecture

Le backend suit une Clean Architecture pragmatique :

```text
domain          modèles et règles indépendants du framework
application     cas d'usage et ports entrants/sortants
infrastructure  contrôleurs REST, configuration et dépôts en mémoire
```

Les dépendances pointent vers le cœur : les services applicatifs connaissent uniquement les interfaces de dépôts. Spring assemble les implémentations dans `ApplicationConfiguration`.

Le stockage en mémoire est un choix adapté au temps du kata. Les commandes sont réinitialisées à chaque redémarrage, les images ne sont pas conservées et seules leurs métadonnées sont enregistrées. En production, les ports permettraient de remplacer ces adaptateurs par une base de données et un stockage objet sans modifier les règles métier.

## API

### `GET /api/orders`

Retourne l'historique du client. L'en-tête optionnel `X-Customer-Id` simule le client authentifié.

### `POST /api/refunds`

Requête `multipart/form-data` :

- `orderId`
- `itemId`
- `description`
- `evidence`

Répond `201 Created` avec la demande créée. Une règle métier non satisfaite répond `422 Unprocessable Entity` au format Problem Details.

## Limites et suite possible

- authentification réelle et autorisations ;
- persistance SQL et stockage sécurisé des images ;
- analyse antivirus et vérification du contenu réel du fichier ;
- consultation et traitement du statut d'une demande ;
- tests d'intégration de l'API et tests de composants Vue ;
- accessibilité complète du dialogue (focus trap et retour du focus).

