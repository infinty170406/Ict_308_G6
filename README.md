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

Pour éviter que chacun ne crée ses propres versions des modèles de données et des signatures de méthodes, voici la spécification commune et le diagramme de classes UML à respecter obligatoirement sur toutes les branches.

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

### Spécification des Attributs et Comportements

#### A. Les Modèles (core/model)
*   **`Trajet`** : Identifie le train (ex: Yaoundé -> Douala, départ à 14h30, tarif de base 5000 FCFA).
*   **`Siege`** : Représente un siège avec son numéro, sa catégorie (`VIP` avec supplément de 3000 FCFA, `PREMIUM` avec supplément de 1000 FCFA, ou `CLASSIQUE` sans supplément), et son état de disponibilité.
*   **`Billet`** : Contient la référence unique générée automatiquement (ex: `CR-YDE-DLA-12345`), le nom et la CNI du passager, le trajet, le siège, et le prix final calculé (`tarifDeBase + supplement`).

#### B. La Persistance (persistance/dao et exception)
*   **`IBilletDAO`** : Interface commune. L'implémentation `BilletDAOImpl` doit sauvegarder et lire les billets dans un fichier texte structuré (`billets.txt` ou `billets.csv`).
*   **`ImpressionException`** : Exception levée lors d'une défaillance matérielle simulée (ex: `"BOURRAGE_PAPIER"`, `"ENCRE_INSUFFISANTE"`).

#### C. Les Services Métier (core/service)
*   **`ReservationService`** : Centralise la logique métier. C'est ici que l'on vérifie la disponibilité du siège avant de l'attribuer et que l'on fait appel au DAO pour persister la vente.

#### D. Les Écrans Swing (ihm/panels et components)
L'interface graphique est gérée par un `CardLayout` contenant les 3 écrans de l'assistant de vente :
1.  **Écran 1 (Sélection)** : Recherche du trajet et choix du siège sur un plan de wagon interactif.
2.  **Écran 2 (Confirmation)** : Saisie des informations du passager (Nom, CNI) et récapitulatif du prix.
3.  **Écran 3 (Impression)** : Simulation visuelle de l'impression physique du billet avec une jauge de progression.

#### E. L'Asynchronisme (thread/animation)
*   **`ImpressionWorker` (SwingWorker)** : Simule l'impression sur un thread d'arrière-plan pendant 3 à 5 secondes. Il met à jour la barre de progression sur l'IHM et peut aléatoirement lever une `ImpressionException` pour simuler un incident technique (bourrage papier).
