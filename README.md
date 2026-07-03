# 🚄 CAMRAIL INTERCITY - EXAMEN ICT308 (Groupe 6)
## Guide d'Architecture Unifiée, Spécifications et Utilisation du Système

---

## 🌌 1. Présentation & Rôle de Chaque Composant Unifié

Ce projet est la fusion de trois sous-systèmes distincts développés par l'équipe pour construire l'application finale **Camrail Intercity (Cyberpunk 2035 Edition)**. Voici ce que chaque composant a apporté au système :

### 💎 A. Le Module Métier ("Elsa") — *core/model* & *core/service*
Il constitue le cœur logique et les contrats métier de l'application :
* **Modèles Métier** :
  * `Trajet` : Modélise les liaisons ferroviaires, heures de départ, durées et calcule les prix HT, TVA (19.25%) et TTC.
  * `Siege` : Modélise l'entité réservable avec ses différents niveaux de confort (`ClasseVoyage`).
  * `Reservation` : Agrège un trajet, un siège et un passager. Elle génère le format de ticket final et gère l'état du paiement.
* **Contrats d'Interfaces** :
  * `ITicketable` : Contrat standardisé pour tout objet imprimable sous forme de ticket.
  * `ITarifiable` : Méthodes de calcul pour les prix HT, TTC et la TVA.
  * `IReservable` : Méthodes de contrôle des transitions d'états d'une place.
  * `IComparableParPrix` : Algorithmes de tri par valeur financière.
* **Services** :
  * `GestionReservations` : Gestionnaire en mémoire orchestrant les trajets et l'état des sièges au démarrage.

### 💾 B. Le Module Persistance ("Dev_newSystem") — *persistance*
Il fournit le squelette d'écriture et de gestion des sauvegardes physiques :
* `TicketDAO` : Interface définissant les opérations d'accès aux données de réservation.
* `DataAccess` : Classe de stockage qui utilise la sérialisation binaire Java (`.ser`) pour sauvegarder les objets de réservation.
* `FileManager` : Gère la création des dossiers locaux de données et les flux d'entrées/sorties binarisées.
* `TicketWriter` : Écrit la facture textuelle formatée du billet de train directement sur le disque.
* `PersistanceException` & `TicketException` : Gestion d'erreurs d'écriture disque et d'accès.

### 🎨 C. Le Module Graphique ("IHM") — *ihm*
Il fournit l'expérience visuelle Cyberpunk premium, immersive et optimisée :
* **Rendu Haute-Fidélité (Zero-CPU Idle)** : Structure optimisée limitant les repaint répétitifs, utilisant une mise en cache des textures et polices néon.
* **Composants Dynamiques** :
  * `WelcomePanel` : Écran d'accueil futuriste avec locomotive animée.
  * `SelectionPanel` : Grille de sièges interactive (`SiegeGrid`) s'adaptant à la taille de l'écran, affichage interactif des trajets (`TrainTable`), et carte holographique des gares (`RailNetworkMap`).
  * `ConfirmationPanel` : Saisie des données de voyage, vérification bancaire biométrique et récapitulatif translucide (Glassmorphism).
  * `ImpressionPanel` : Processus d'impression asynchrone sécurisé avec jauge de progression.

### 🗄️ D. La Couche Base de Données (SQLite JDBC)
Nous avons implémenté une double persistance hybride via SQLite :
* `DatabaseAccess` : Implémentation SQL du `TicketDAO` insérant les réservations dans `data/camrail.db`.
* **Automatique** : Initialisation automatique de la base et de la table `reservations` sans configuration externe complexe requise.

---

## 🛠️ 2. Guide d'Utilisation et Commandes

### 📋 Prérequis
* **JDK 17** ou supérieur installé.
* **Apache Maven** installé.

### 🚀 Compilation
Pour compiler le projet et télécharger les dépendances requises (notamment le pilote SQLite) :
```bash
mvn clean compile
```

### 🚄 Lancement de l'Application
Pour démarrer l'IHM interactive unifiée de Camrail Intercity :
```bash
mvn exec:java
```

---

## 📂 3. Emplacement des Données Générées

Une fois qu'une réservation est validée dans l'application, les fichiers suivants sont créés dans le répertoire racine du projet :

1. **`data/camrail.db`** : Base de données SQLite relationnelle contenant la table `reservations`.
2. **`data/reservations.ser`** : Fichier de sauvegarde binaire sérialisé contenant la liste complète des objets `Reservation`.
3. **`tickets/ticket_CAR-YYYYMMDD-[RAND].txt`** : Reçu d'embarquement formaté prêt à l'impression.
