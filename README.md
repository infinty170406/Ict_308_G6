# Équipe Core & Métier - Projet ICT308 (Groupe 6)
## Système de Vente et d'Impression des Billets de Train (Carnail Intercity)

---

## 📦 Contenu du livrable

| Fichier | Type | Description |
|---------|------|-------------|
| `ITicketable.java` | Interface | Contrat pour les objets imprimables en ticket |
| `IReservable.java` | Interface | Contrat pour les objets réservables |
| `ITarifiable.java` | Interface | Contrat pour les objets avec prix et taxes |
| `IComparableParPrix.java` | Interface | Contrat pour la comparaison par prix |
| `Produit.java` | Classe abstraite | Classe de base pour tous les produits |
| `ClasseVoyage.java` | Enum | Classes de voyage (Première, Business, Économique) |
| `Trajet.java` | Classe | Hérite de Produit, implémente ITarifiable |
| `Siege.java` | Classe | Hérite de Produit, implémente IReservable et Comparable |
| `Reservation.java` | Classe | Implémente ITicketable |
| `GestionReservations.java` | Classe | Service métier (backend) |
| `Test.java` | Test | Classe de validation du bon fonctionnement |

---

## 🔗 Interfaces exposées

### 1. `ITicketable`
```java
public interface ITicketable {
    double getPrixTotal();
    String getNumeroTicket();
    String getDescriptionTicket();
}