````markdown
# Camrail Intercity - Système de Vente et d'Impression de Billets

## Rapport d'Architecture Technique & README d'Équipe
### *Examen ICT308*

---

# 1. Objectif du Projet

Développer une application logicielle pour une **borne interactive Camrail Intercity** permettant aux voyageurs de :

- Réserver un trajet de train par étapes ;
- Choisir leur classe de voyage ;
- Sélectionner leur siège ;
- Simuler l'impression physique d'un reçu officiel.

Ce document décrit l'organisation technique du projet afin de :

- éviter les conflits de code ;
- permettre le développement parallèle des équipes ;
- faciliter l'intégration finale sans bug.

---

# 2. Architecture Générale du Projet (MVC / Modulaire)

Le projet adopte une architecture stricte en **packages**.

> **Règles importantes**
>
> - L'IHM ne manipule jamais directement les fichiers.
> - Les classes métier ne créent jamais de composants Swing.
> - Chaque package possède une responsabilité unique.

## Structure du projet

```text
src/
└── com/
    └── camrail/
        ├── Main.java
        │
        ├── core/                         # Équipe 1 : Métier
        │   ├── model/
        │   │   ├── Trajet.java
        │   │   ├── Siege.java
        │   │   └── Billet.java
        │   │
        │   └── service/
        │       ├── TarifService.java
        │       ├── ReservationService.java
        │       └── BilletService.java
        │
        ├── persistance/                  # Équipe 2
        │   ├── dao/
        │   │   └── TicketWriter.java
        │   │
        │   └── exception/
        │       ├── TicketStorageException.java
        │       └── FileLockedException.java
        │
        ├── ihm/                          # Équipe 3
        │   ├── components/
        │   ├── panels/
        │   │   ├── ChoixTrajetPanel.java
        │   │   ├── ChoixSiegePanel.java
        │   │   └── PaiementPanel.java
        │   │
        │   └── MainFrame.java
        │
        └── thread/                       # Équipe 4
            └── animation/
                ├── ImpressionWorker.java
                └── ProgressAnimation.java
```

---

# 3. Répartition des Rôles (10 Étudiants)

Le projet est divisé en **4 sous-équipes**.

---

# Équipe 1 — Core & Métier (3 étudiants)

## Mission

Développer toute la logique métier de l'application.

### Étudiant 1 — Modélisation POO

Créer les classes :

- `Trajet`
- `Siege`
- `Billet`

Chaque classe doit contenir :

- attributs ;
- constructeurs ;
- getters/setters ;
- méthodes utiles (`toString()`, etc.).

### Étudiant 2 — Logique Métier

Développer :

- calcul des tarifs ;
- gestion des classes (Première, Business, Économique) ;
- calcul des taxes ;
- génération d'un code unique de sécurité.

### Étudiant 3 — Gestion des Collections

Créer les structures mémoire :

- `List<Trajet>`
- `List<Siege>`
- `Map`

Responsabilités :

- stocker les trajets disponibles ;
- gérer les sièges libres ;
- réserver les sièges.

---

# Équipe 2 — Persistance & Données (2 étudiants)

## Mission

Gérer la sauvegarde des billets et les erreurs de fichiers.

### Étudiant 4 — Module d'Écriture

Créer une classe utilisant :

- `FileWriter`
- `PrintWriter`

Méthode principale :

```java
public void genererTicket(Billet billet)
```

Cette méthode génère un fichier :

```text
ticket_XXXXXX.txt
```

Le contenu doit ressembler à un véritable ticket de caisse.

### Étudiant 5 — Gestion des Exceptions

Créer les exceptions personnalisées :

```text
TicketStorageException
FileLockedException
```

Simuler des erreurs telles que :

- dossier inaccessible ;
- fichier verrouillé ;
- erreur d'écriture.

---

# Équipe 3 — Interface Swing (3 étudiants)

## Mission

Créer une interface utilisateur moderne et intuitive.

### Étudiant 6 — Fenêtre Principale

Créer :

- `JFrame`
- `BorderLayout`
- `CardLayout`

Responsabilités :

- navigation entre les écrans ;
- changement de vues.

### Étudiant 7 — Écrans 1 & 2

Créer :

#### Écran 1

- choix du départ ;
- choix de la destination ;
- choix de la classe.

#### Écran 2

Créer une grille de sièges :

- vert : libre ;
- rouge : occupé.

### Étudiant 8 — Écran 3

Créer :

- récapitulatif du billet ;
- bouton **Payer** ;
- bouton **Imprimer**.

Responsabilités :

- gérer tous les `ActionListener` ;
- connecter l'IHM aux autres modules.

---

# Équipe 4 — Multithreading (2 étudiants)

## Mission

Éviter que l'interface se bloque pendant l'impression.

### Étudiant 9 — Impression Asynchrone

Créer un :

- `SwingWorker`
- ou `Thread`

Simulation :

- impression pendant **2,5 secondes**.

### Étudiant 10 — Animation

Créer une animation :

- barre de progression ;
- texte :

```text
Impression du billet en cours...
```

À la fin :

- retour à l'écran 1 ;
- suppression des anciennes sélections ;
- remise à zéro complète.

---

# 4. Matrice de Flux et Synchronisation

| Étape | Action | Équipe | Dépendance |
|--------|--------|---------|------------|
| 1 | Création des modèles métier | Équipe 1 | Aucune |
| 2 | Création de l'IHM (CardLayout) | Équipe 3 | Structure des écrans |
| 3 | Génération des fichiers `ticket_XXXXXX.txt` | Équipe 2 | Classes métier |
| 4 | Développement du `SwingWorker` | Équipe 4 | Écran 3 |
| 5 | Intégration finale | Toutes | Tous les modules |

---

# 5. Workflow Git

Afin d'éviter les conflits de code :

## Une branche par équipe

```text
main
├── feature/core-metier
├── feature/persistance
├── feature/ihm-swing
└── feature/multithreading
```

**Règle :** il est interdit de pousser directement sur la branche `main`.

---

# 6. Règles de Développement

## Respect des signatures

Si l'équipe Persistance crée :

```java
public void genererTicket(Billet billet)
```

Toutes les autres équipes doivent appeler **exactement cette méthode**.

Aucun changement de :

- nom ;
- paramètres ;
- type de retour.

## Interdictions

Ne jamais utiliser :

```java
setLayout(null);
```

ou

```java
setBounds(...);
```

## Layouts autorisés

- `BorderLayout`
- `GridLayout`
- `CardLayout`
- `FlowLayout` (si nécessaire)

---

# 7. Résultat Attendu

À la fin du projet, l'application devra permettre :

1. Choisir un trajet.
2. Choisir une classe.
3. Choisir un siège.
4. Simuler le paiement.
5. Générer un ticket texte.
6. Simuler une impression de **2,5 secondes**.
7. Réinitialiser automatiquement la borne.

---

# Résumé des Responsabilités

| Équipe | Domaine | Membres |
|---------|----------|----------|
| Équipe 1 | Métier & POO | 3 |
| Équipe 2 | Persistance | 2 |
| Équipe 3 | Interface Swing | 3 |
| Équipe 4 | Multithreading | 2 |

**Total : 10 étudiants**
````
