package com.camrail.core.service;

import com.camrail.core.model.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestionnaire central des réservations.
 * C'est le service métier (backend) qui orchestre toutes les opérations.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public class GestionReservations implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Liste de toutes les réservations */
    private List<Reservation> reservations;

    /** Liste des trajets disponibles */
    private List<Trajet> trajetsDisponibles;

    /** Liste de tous les sièges */
    private List<Siege> siegeDisponibles;

    /**
     * Constructeur : initialise les données de démonstration
     */
    public GestionReservations() {
        this.reservations = new ArrayList<>();
        this.trajetsDisponibles = new ArrayList<>();
        this.siegeDisponibles = new ArrayList<>();
        initialiserDonnees();
    }

    /**
     * Initialise les données de démonstration
     */
    private void initialiserDonnees() {
        // ===== TRAJETS DISPONIBLES =====
        trajetsDisponibles.add(new Trajet("Yaoundé", "Douala", "06:30", 18000, 135));
        trajetsDisponibles.add(new Trajet("Douala", "Yaoundé", "09:00", 18000, 135));
        trajetsDisponibles.add(new Trajet("Yaoundé", "Ngaoundéré", "20:00", 32000, 660));
        trajetsDisponibles.add(new Trajet("Ngaoundéré", "Yaoundé", "19:30", 32000, 645));
        trajetsDisponibles.add(new Trajet("Douala", "Bafoussam", "08:15", 16000, 225));
        trajetsDisponibles.add(new Trajet("Bafoussam", "Douala", "14:30", 16000, 225));
        trajetsDisponibles.add(new Trajet("Yaoundé", "Kribi", "07:30", 12000, 180));
        trajetsDisponibles.add(new Trajet("Kribi", "Yaoundé", "16:00", 12000, 180));

        // ===== SIÈGES : 10 Première (1-10), 10 Business (11-20), 20 Économique (21-40) =====
        int num = 1;
        for (int i = 0; i < 10; i++) {
            siegeDisponibles.add(new Siege(num++, ClasseVoyage.PREMIERE));
        }
        for (int i = 0; i < 10; i++) {
            siegeDisponibles.add(new Siege(num++, ClasseVoyage.BUSINESS));
        }
        for (int i = 0; i < 20; i++) {
            siegeDisponibles.add(new Siege(num++, ClasseVoyage.ECONOMIQUE));
        }
    }

    // ===== MÉTHODES DE GESTION DES RÉSERVATIONS =====

    /**
     * Crée une nouvelle réservation
     * 
     * @param trajet Trajet choisi
     * @param siege  Siège choisi
     * @param nom    Nom du passager
     * @param prenom Prénom du passager
     * @return La réservation créée
     * @throws IllegalStateException si le siège n'est pas disponible
     */
    public Reservation creerReservation(Trajet trajet, Siege siege,
            String nom, String prenom) {
        if (!siege.isDisponible()) {
            throw new IllegalStateException(
                    "Le siège " + siege.getNumero() + " n'est pas disponible !");
        }
        Reservation reservation = new Reservation(trajet, siege, nom, prenom);
        reservations.add(reservation);
        return reservation;
    }

    /**
     * Crée une nouvelle réservation (version simplifiée)
     * 
     * @param trajet     Trajet choisi
     * @param siege      Siège choisi
     * @param nomComplet Nom complet du passager
     * @return La réservation créée
     */
    public Reservation creerReservation(Trajet trajet, Siege siege, String nomComplet) {
        return creerReservation(trajet, siege, nomComplet, "");
    }

    /**
     * Annule une réservation non payée
     * 
     * @param numeroTicket Numéro du ticket à annuler
     * @return true si annulée, false sinon
     */
    public boolean annulerReservation(String numeroTicket) {
        for (Reservation r : reservations) {
            if (r.getNumeroTicket().equals(numeroTicket) && !r.isPaye()) {
                r.getSiege().liberer();
                return reservations.remove(r);
            }
        }
        return false;
    }

    /**
     * Récupère une réservation par son numéro
     * 
     * @param numeroTicket Numéro du ticket
     * @return La réservation trouvée, ou null
     */
    public Reservation getReservationParNumero(String numeroTicket) {
        return reservations.stream()
                .filter(r -> r.getNumeroTicket().equals(numeroTicket))
                .findFirst()
                .orElse(null);
    }

    // ===== MÉTHODES DE RECHERCHE =====

    /**
     * Recherche des trajets par villes
     * 
     * @param depart  Ville de départ
     * @param arrivee Ville d'arrivée
     * @return Liste des trajets correspondants
     */
    public List<Trajet> trouverTrajets(String depart, String arrivee) {
        return trajetsDisponibles.stream()
                .filter(t -> t.getVilleDepart().equalsIgnoreCase(depart))
                .filter(t -> t.getVilleArrivee().equalsIgnoreCase(arrivee))
                .collect(Collectors.toList());
    }

    /**
     * Trouve les sièges disponibles d'une classe donnée
     * 
     * @param classe Classe de voyage
     * @return Liste des sièges disponibles triés
     */
    public List<Siege> trouverSiegesDisponibles(ClasseVoyage classe) {
        return siegeDisponibles.stream()
                .filter(s -> s.estDeClasse(classe))
                .filter(Siege::isDisponible)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Trouve les sièges disponibles pour un trajet et une classe
     * 
     * @param trajet Trajet (non utilisé car tous les sièges sont disponibles pour
     *               tous les trajets)
     * @param classe Classe de voyage
     * @return Liste des sièges disponibles triés
     */
    public List<Siege> trouverSiegesDisponiblesPourTrajet(Trajet trajet, ClasseVoyage classe) {
        return trouverSiegesDisponibles(classe);
    }

    /**
     * Trouve un siège par son numéro
     * 
     * @param numero Numéro du siège
     * @return Le siège trouvé, ou null
     */
    public Siege getSiegeParNumero(int numero) {
        return siegeDisponibles.stream()
                .filter(s -> s.getNumero() == numero)
                .findFirst()
                .orElse(null);
    }

    // ===== MÉTHODES DE STATISTIQUES =====

    /**
     * Retourne le nombre total de réservations
     * 
     * @return Nombre de réservations
     */
    public int getNombreReservations() {
        return reservations.size();
    }

    /**
     * Retourne le nombre de réservations payées
     * 
     * @return Nombre de réservations payées
     */
    public int getNombreReservationsPayees() {
        return (int) reservations.stream()
                .filter(Reservation::isPaye)
                .count();
    }

    /**
     * Calcule le chiffre d'affaires total
     * 
     * @return Chiffre d'affaires en FCFA
     */
    public double getChiffreAffairesTotal() {
        return reservations.stream()
                .filter(Reservation::isPaye)
                .mapToDouble(Reservation::getPrixTotal)
                .sum();
    }

    /**
     * Retourne les réservations payées
     * 
     * @return Liste des réservations payées
     */
    public List<Reservation> getReservationsPayees() {
        return reservations.stream()
                .filter(Reservation::isPaye)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les réservations triées par prix (décroissant)
     * 
     * @return Liste triée par prix
     */
    public List<Reservation> getReservationsTrieesParPrix() {
        return reservations.stream()
                .sorted((r1, r2) -> Double.compare(r2.getPrixTotal(), r1.getPrixTotal()))
                .collect(Collectors.toList());
    }

    /**
     * Retourne les réservations par classe
     * 
     * @param classe Classe de voyage
     * @return Liste des réservations pour cette classe
     */
    public List<Reservation> getReservationsParClasse(ClasseVoyage classe) {
        return reservations.stream()
                .filter(r -> r.getSiege().getClasse() == classe)
                .collect(Collectors.toList());
    }

    // ===== GETTERS (READ-ONLY) =====

    /**
     * Retourne une copie non modifiable de la liste des réservations
     * 
     * @return Liste des réservations (lecture seule)
     */
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    /**
     * Retourne une copie non modifiable de la liste des trajets
     * 
     * @return Liste des trajets (lecture seule)
     */
    public List<Trajet> getTrajetsDisponibles() {
        return Collections.unmodifiableList(trajetsDisponibles);
    }

    /**
     * Retourne une copie non modifiable de la liste des sièges
     * 
     * @return Liste des sièges (lecture seule)
     */
    public List<Siege> getSiegeDisponibles() {
        return Collections.unmodifiableList(siegeDisponibles);
    }

    // ===== MÉTHODES DE RÉINITIALISATION =====

    /**
     * Réinitialise les données (pour les tests)
     */
    public void reinitialiser() {
        this.reservations.clear();
        this.trajetsDisponibles.clear();
        this.siegeDisponibles.clear();
        initialiserDonnees();
    }

    // ===== MÉTHODES Object =====

    @Override
    public String toString() {
        return String.format(
                "GestionReservations [réservations: %d, trajets: %d, sièges: %d]",
                reservations.size(),
                trajetsDisponibles.size(),
                siegeDisponibles.size());
    }
}
