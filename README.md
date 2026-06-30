# Guide d'Architecture Technique et README

## Projet 6 : Système de Vente et d'Impression de Billets (Camrail Intercity) - Examen ICT308

---

### Objectif du Document
Structurer le travail pour l'équipe de 10 personnes afin d'éviter les conflits de code, définir précisément le rôle de chaque sous-groupe et garantir une architecture logicielle propre et modulaire (MVC) respectant scrupuleusement le cahier des charges.

---

## 1. Architecture Générale du Projet (Modèle-Vue-Contrôleur)

Afin de s'assurer que les développements des 10 membres s'assemblent sans friction, le projet adoptera une architecture standardisée découpée en packages stricts. Aucun code de l'IHM ne doit manipuler directement les fichiers, et aucune classe métier ne doit instancier de composant Swing.

```text
src/
└── com/
    └── camrail/
        ├── Main.java                        # Point d'entrée de l'application
        ├── core/                            # Équipe 1 : Métier & Logique
        │   ├── model/                       # Classes de données (Trajet, Siege, Billet)
        │   └── service/                     # Algorithmes de gestion et de validation
        ├── persistance/                     # Équipe 2 : Fichiers & Exception
        │   ├── dao/                         # Interface et implémentation d'écriture
        │   └── exception/                   # Exceptions matérielles simulées
        ├── ihm/                             # Équipe 3 : Interface Swing
        │   ├── components/                  # Éléments réutilisables (Boutons, Tables)
        │   └── panels/                      # Assistant Écran 1, 2, 3 géré par CardLayout
        └── thread/                          # Équipe 4 : Multithreading & Async
            └── animation/                   # SwingWorker, Simulateur d'Impression
```

---

## 2. Modélisation UML & Contrats d'Interface

Pour travailler sereinement en équipe, nous avons besoin de la modélisation UML du travail pour avoir une vue plus claire de ce qu'il y aura lieu de faire. Sinon chacun travaillera avec ce qu'il aura créé de son côté et ce ne sera pas évident.

Voici donc le diagramme de classes et le diagramme de composants à respecter obligatoirement sur toutes les branches.

### Diagramme de Classes UML (Mermaid)

```mermaid
classDiagram
    class Trajet {
        - String id
        - String villeDepart
        - String villeArrivee
        - String heureDepart
        - double tarifDeBase
        + getId() String
        + getVilleDepart() String
        + getVilleArrivee() String
        + getHeureDepart() String
        + getTarifDeBase() double
    }

    class Siege {
        - String numero
        - String categorie
        - boolean disponible
        - double supplement
        + getNumero() String
        + getCategorie() String
        + isDisponible() boolean
        + setDisponible(boolean) void
        + getSupplement() double
    }

    class Billet {
        - String referenceUnique
        - String nomPassager
        - String cniPassager
        - Trajet trajet
        - Siege siege
        - double prixTotal
        + getReferenceUnique() String
        + getNomPassager() String
        + getCniPassager() String
        + getTrajet() Trajet
        + getSiege() Siege
        + getPrixTotal() double
    }

    class IBilletDAO {
        <<interface>>
        + enregistrerBillet(Billet billet) void
        + trouverParReference(String ref) Billet
        + listerTous() List~Billet~
    }

    class BilletDAOImpl {
        - String FILE_PATH
        + enregistrerBillet(Billet billet) void
        + trouverParReference(String ref) Billet
        + listerTous() List~Billet~
    }

    class ReservationService {
        - IBilletDAO billetDao
        - List~Trajet~ trajetsDisponibles
        - Map~Trajet, List~Siege~~ cartographieSieges
        + rechercherTrajets(String dep, String arr) List~Trajet~
        + obtenirSiegesDisponibles(Trajet trajet) List~Siege~
        + effectuerReservation(String nom, String cni, Trajet t, Siege s) Billet
    }

    class ImpressionException {
        <<exception>>
        - String codeErreur
        + getCodeErreur() String
    }

    Billet "1" o-- "1" Trajet : associe
    Billet "1" o-- "1" Siege : reserve
    BilletDAOImpl ..|> IBilletDAO : implemente
    ReservationService --> IBilletDAO : utilise
    ReservationService "1" o-- "*" Trajet : gere
```

### Diagramme de Composants (Architecture de Flux)

```mermaid
graph TD
    subgraph Client [IHM - Interface Homme-Machine]
        Panels[ihm.panels] --> Components[ihm.components]
    end

    subgraph Business [Core - Logique Métier]
        Service[core.service] --> Model[core.model]
    end

    subgraph Data [Persistance - Accès Données]
        DAO[persistance.dao] --> Exception[persistance.exception]
    end

    subgraph Async [Thread - Traitement Asynchrone]
        Animation[thread.animation]
    end

    %% Dépendances entre composants
    Panels --> Service
    Service --> DAO
    Animation --> Panels
    Animation --> Exception
```
