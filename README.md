# 🚄 CAMRAIL INTERCITY - EXAMEN ICT308 (Groupe 6)
# Guide d'Architecture Unifiée, Spécifications et Utilisation du Système

---

# 🌌 1. Présentation Générale

**CAMRAIL INTERCITY** est une application Java développée dans le cadre de l'UE **ICT308**.

L'objectif principal est de proposer un système complet de réservation de billets de train reproduisant le fonctionnement d'une véritable compagnie ferroviaire.

L'utilisateur peut :

- Consulter les trajets disponibles ;
- Choisir une destination ;
- Sélectionner une classe de voyage ;
- Choisir un siège disponible ;
- Effectuer une réservation ;
- Générer automatiquement un billet ;
- Sauvegarder la réservation ;
- Imprimer son ticket.

Le projet a été conçu selon une **architecture modulaire en couches**, permettant une séparation claire entre :

- l'interface graphique ;
- la logique métier ;
- la persistance des données ;
- les utilitaires.

Cette architecture facilite énormément :

- la maintenance ;
- les évolutions futures ;
- la réutilisation du code ;
- les tests.

---

# 🏗️ 2. Architecture Générale

Le système suit le schéma suivant :

```
                 Utilisateur
                      │
                      ▼
        Interface Graphique (IHM)
                      │
                      ▼
          Couche Métier (Core)
                      │
                      ▼
      Couche Persistance (DAO)
                      │
                      ▼
      Base SQLite + Fichiers locaux
```

Chaque couche possède une responsabilité bien définie.

Aucune couche ne réalise le travail d'une autre.

---

# 🌌 3. Présentation & Rôle de Chaque Composant Unifié

Le projet est la fusion de plusieurs sous-systèmes développés indépendamment avant leur intégration finale.

---

# 💎 A. Module Métier (core)

Le dossier **core** représente le cerveau de l'application.

Il contient toutes les règles métier.

Aucune logique métier importante ne doit être écrite dans les interfaces graphiques.

Le module est organisé en trois parties.

---

## 📁 core/model

Il contient toutes les entités du système.

Chaque classe représente un objet réel.

### Trajet.java

Représente un voyage ferroviaire.

Il contient notamment :

- gare de départ
- gare d'arrivée
- heure
- durée
- prix HT
- TVA (19.25 %)
- prix TTC

---

### Siege.java

Représente un siège.

Chaque siège possède :

- un numéro
- une classe
- un état (Libre / Réservé)

---

### Reservation.java

Représente une réservation complète.

Elle regroupe :

- le passager
- le trajet
- le siège
- le montant payé
- le code de réservation

Elle est également capable de générer le contenu du ticket.

---

### ClasseVoyage.java

Définit les différentes classes disponibles :

- Économique
- Première Classe
- VIP

---

### Produit.java

Classe générique représentant un élément pouvant être réservé.

---

### Interfaces

Le projet applique fortement la programmation orientée interface.

#### ITicketable

Définit tout objet capable de produire un billet.

---

#### ITarifiable

Déclare les méthodes :

- calcul HT
- calcul TVA
- calcul TTC

---

#### IReservable

Déclare les méthodes permettant :

- réserver
- libérer
- vérifier la disponibilité

---

#### IComparableParPrix

Permet le tri des objets selon leur prix.

---

## 📁 core/service

Contient la logique métier.

---

### GestionReservations.java

C'est la classe principale du système.

Elle orchestre :

- la liste des trajets
- la liste des sièges
- les réservations
- les annulations
- les disponibilités

Toutes les opérations importantes transitent par cette classe.

---

## 📁 core/util

Contient les utilitaires.

---

### GenerateurCode.java

Produit automatiquement un identifiant unique.

Exemple :

```
CAR-20260710-4587
```

Chaque réservation possède son propre identifiant.

---

# 💾 B. Module Persistance (persistance)

Cette couche est entièrement responsable du stockage des données.

Elle est indépendante du reste de l'application.

Ainsi, il serait possible de remplacer SQLite par MySQL sans modifier les interfaces graphiques.

---

## 📁 persistance

### FileManager.java

Gère :

- la création des dossiers
- la lecture
- l'écriture
- la sérialisation

---

### TicketWriter.java

Produit automatiquement un ticket texte prêt à être imprimé.

---

## 📁 persistance/dao

DAO signifie **Data Access Object**.

Cette couche communique avec les données.

---

### TicketDAO

Interface définissant les opérations :

- enregistrer
- rechercher
- supprimer
- modifier

---

### DataAccess

Sauvegarde les objets Reservation dans :

```
reservations.ser
```

grâce à la sérialisation Java.

---

### DatabaseAccess

Version SQLite du DAO.

Elle communique avec :

```
data/camrail.db
```

Toutes les réservations sont automatiquement enregistrées dans la base.

---

## 📁 persistance/exception

Contient les exceptions personnalisées.

---

### PersistanceException

Erreur générale de sauvegarde.

---

### TicketException

Erreur spécifique aux billets.

---

# 🎨 C. Module Interface Graphique (ihm)

Le dossier **ihm** représente toute l'expérience utilisateur.

Aucune règle métier importante ne s'y trouve.

Il est organisé en plusieurs sous-parties.

---

## 📁 MainFrame.java

Fenêtre principale.

Elle assemble toutes les pages.

Elle permet la navigation dans toute l'application.

---

## 📁 Theme.java

Centralise :

- couleurs
- polices
- styles
- thème clair
- thème sombre

Toute modification graphique passe par cette classe.

---

# 📁 ihm/components

Ce dossier contient des composants réutilisables.

Ils évitent la duplication du code.

---

### CustomButton

Boutons personnalisés.

---

### FuturisticBackground

Dessine le fond Cyberpunk.

---

### HolographicTicket

Affiche un billet holographique.

---

### JourneyTimeline

Montre :

```
Départ
   │
   ▼
Arrêts
   │
   ▼
Destination
```

---

### ProgressPanel

Affiche les étapes :

```
Choix trajet

↓

Choix siège

↓

Paiement

↓

Impression
```

---

### RailNetworkMap

Carte interactive des gares.

---

### SiegeGrid

Affiche tous les sièges.

Les sièges réservés deviennent indisponibles.

---

### TrainTable

Affiche les trajets disponibles.

---

# 📁 ihm/panels

Chaque Panel représente une véritable page.

---

### WelcomePanel

Première page.

Présentation du système.

Animation de locomotive.

---

### SelectionPanel

Permet :

- choisir un trajet
- choisir une classe
- sélectionner un siège

---

### ConfirmationPanel

Affiche :

- récapitulatif
- prix
- informations voyageur
- validation

---

### ImpressionPanel

Dernière étape.

Permet :

- afficher le ticket
- sauvegarder
- imprimer

---

# 📁 ihm/javafx

Contient la version JavaFX.

---

### CamrailJavaFXApp

Version JavaFX complète.

---

### ConsultDB

Permet de consulter directement les réservations.

---

### DataStoreFX

Stockage temporaire utilisé par JavaFX.

---

### stylefx.css

Style principal.

---

### lightfx.css

Version claire du thème.

---

# 🗄️ D. Couche Base de Données

Le système utilise une **double persistance hybride**.

Les données sont sauvegardées simultanément :

- dans une base SQLite ;
- dans un fichier sérialisé Java.

Cela permet de sécuriser les données.

---

# 📂 4. Structure complète du projet

```
src/
│
├── Main.java
│
├── core
│   ├── model
│   ├── service
│   └── util
│
├── ihm
│   ├── components
│   ├── panels
│   ├── javafx
│   └── Theme.java
│
├── persistance
│   ├── dao
│   ├── exception
│   ├── FileManager.java
│   └── TicketWriter.java
│
└── thread
```

---

# ⚙️ 5. Déroulement complet d'une réservation

Le fonctionnement interne du système est le suivant :

```
Ouverture de l'application
            │
            ▼
Page d'accueil
            │
            ▼
Choix du trajet
            │
            ▼
Choix de la classe
            │
            ▼
Choix du siège
            │
            ▼
Calcul automatique du prix
            │
            ▼
Confirmation
            │
            ▼
Création de la réservation
            │
            ▼
Génération du code unique
            │
            ▼
Sauvegarde SQLite
            │
            ▼
Sauvegarde .SER
            │
            ▼
Création du ticket
            │
            ▼
Impression
```

---

# 🛠️ 6. Guide d'Utilisation

## 📋 Prérequis

- Java JDK 17+
- Maven 3.9+

---

## Compilation

```bash
mvn clean compile
```

---

## Lancement

```bash
mvn exec:java
```

---

# 📂 7. Données générées

Après chaque réservation, plusieurs fichiers sont créés.

## SQLite

```
data/camrail.db
```

Contient toutes les réservations.

---

## Sauvegarde Java

```
data/reservations.ser
```

Contient la liste complète des objets Reservation.

---

## Ticket

```
tickets/
    ticket_CAR-YYYYMMDD-XXXX.txt
```

Billet imprimable.

---

# 💻 8. Technologies utilisées

- Java 17
- Swing
- JavaFX
- Maven
- SQLite JDBC
- CSS JavaFX

---

# 🎯 9. Principes de conception

Le projet respecte plusieurs bonnes pratiques de développement :

- Architecture en couches
- Séparation des responsabilités
- Programmation Orientée Objet (POO)
- Interfaces métier
- Réutilisation des composants graphiques
- Persistance indépendante
- Gestion centralisée des exceptions
- Modularité
- Extensibilité

---

# 📊 10. Résumé des responsabilités

| Dossier | Responsabilité |
|----------|----------------|
| Main | Point d'entrée de l'application |
| core/model | Entités métier |
| core/service | Logique métier |
| core/util | Utilitaires |
| ihm | Interface graphique |
| ihm/components | Composants graphiques réutilisables |
| ihm/panels | Pages de l'application |
| ihm/javafx | Version JavaFX |
| persistance | Sauvegarde des données |
| persistance/dao | Accès aux données |
| persistance/exception | Gestion des erreurs |
| thread | Tâches asynchrones et animations |

---

# ✅ 11. Conclusion

CAMRAIL INTERCITY est une application de réservation ferroviaire construite autour d'une architecture modulaire où chaque dossier possède une responsabilité clairement définie.

Cette organisation permet :

- une compréhension rapide du projet par tout nouveau développeur ;
- une maintenance simplifiée ;
- une meilleure évolutivité ;
- une séparation stricte entre la logique métier, l'interface graphique et la persistance des données.

Grâce à cette architecture, toute nouvelle fonctionnalité peut être intégrée avec un impact minimal sur les autres composants du système, garantissant ainsi la stabilité et la pérennité du projet.
