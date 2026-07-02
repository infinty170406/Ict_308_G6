package model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente un trajet ferroviaire.
 * Hérite de Produit (classe abstraite) et implémente ITarifiable.
 * 
 * Un trajet a une ville de départ, une ville d'arrivée,
 * un horaire, une durée et un prix de base.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public class Trajet extends Produit implements ITarifiable, Serializable {

    private static final long serialVersionUID = 1L;

    /** Ville de départ */
    private String villeDepart;

    /** Ville d'arrivée */
    private String villeArrivee;

    /** Horaire de départ */
    private LocalTime horaireDepart;

    /** Durée en minutes */
    private int dureeMinutes;

    /** Taux de TVA appliqué (19.25%) */
    public static final double TVA = 0.1925;

    /** Formateur d'heure */
    private static final DateTimeFormatter FORMAT_HEURE = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Constructeur complet
     * 
     * @param villeDepart   Ville de départ
     * @param villeArrivee  Ville d'arrivée
     * @param horaireDepart Horaire de départ (format "HH:mm")
     * @param prixBase      Prix de base en FCFA
     * @param dureeMinutes  Durée en minutes
     */
    public Trajet(String villeDepart, String villeArrivee, String horaireDepart,
            double prixBase, int dureeMinutes) {
        super(villeDepart + " → " + villeArrivee, prixBase);

        if (villeDepart == null || villeDepart.trim().isEmpty()) {
            throw new IllegalArgumentException("La ville de départ est obligatoire !");
        }
        if (villeArrivee == null || villeArrivee.trim().isEmpty()) {
            throw new IllegalArgumentException("La ville d'arrivée est obligatoire !");
        }
        if (dureeMinutes <= 0) {
            throw new IllegalArgumentException("La durée doit être positive !");
        }

        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.horaireDepart = LocalTime.parse(horaireDepart);
        this.dureeMinutes = dureeMinutes;
    }

    // ===== IMPLÉMENTATION DE ITarifiable =====

    /**
     * Calcule le prix TTC du trajet (sans coefficient de classe)
     * 
     * @return Prix TTC en FCFA
     */
    @Override
    public double calculerPrixTTC() {
        return Math.round(prixBase * (1 + TVA) * 100.0) / 100.0;
    }

    /**
     * Calcule le prix HT du trajet (sans coefficient de classe)
     * 
     * @return Prix HT en FCFA
     */
    @Override
    public double calculerPrixHT() {
        return prixBase;
    }

    /**
     * Calcule la TVA du trajet (sans coefficient de classe)
     * 
     * @return Montant de la TVA en FCFA
     */
    @Override
    public double calculerTVA() {
        return Math.round(prixBase * TVA * 100.0) / 100.0;
    }

    // ===== MÉTHODES MÉTIER SPÉCIFIQUES =====

    /**
     * Calcule le prix TTC avec une classe de voyage
     * 
     * @param classe Classe de voyage (Première, Business, Économique)
     * @return Prix TTC avec coefficient de classe en FCFA
     */
    public double calculerPrixAvecClasse(ClasseVoyage classe) {
        double prixClasse = classe.calculerPrix(prixBase);
        return Math.round(prixClasse * (1 + TVA) * 100.0) / 100.0;
    }

    /**
     * Calcule le prix HT avec une classe de voyage
     * 
     * @param classe Classe de voyage
     * @return Prix HT avec coefficient de classe en FCFA
     */
    public double calculerPrixHT(ClasseVoyage classe) {
        return classe.calculerPrix(prixBase);
    }

    /**
     * Calcule la TVA avec une classe de voyage
     * 
     * @param classe Classe de voyage
     * @return Montant de la TVA avec coefficient de classe en FCFA
     */
    public double calculerTVA(ClasseVoyage classe) {
        return Math.round(calculerPrixHT(classe) * TVA * 100.0) / 100.0;
    }

    /**
     * Calcule l'heure d'arrivée estimée
     * 
     * @return Heure d'arrivée
     */
    public LocalTime getHeureArrivee() {
        return horaireDepart.plusMinutes(dureeMinutes);
    }

    /**
     * Retourne la durée formatée (ex: "4h" ou "3h30")
     * 
     * @return Durée formatée
     */
    public String getDureeFormatee() {
        int heures = dureeMinutes / 60;
        int minutes = dureeMinutes % 60;
        if (minutes == 0) {
            return heures + "h";
        }
        return heures + "h" + minutes;
    }

    /**
     * Retourne l'horaire de départ formaté
     * 
     * @return Horaire au format "HH:mm"
     */
    public String getHoraireDepartStr() {
        return horaireDepart.format(FORMAT_HEURE);
    }

    /**
     * Retourne une description détaillée du trajet
     * 
     * @return Description textuelle
     */
    public String getDescription() {
        return String.format("%s → %s | Départ: %s | Durée: %s | Prix: %,.0f FCFA",
                villeDepart, villeArrivee, getHoraireDepartStr(),
                getDureeFormatee(), prixBase);
    }

    // ===== GETTERS =====

    public String getVilleDepart() {
        return villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public LocalTime getHoraireDepart() {
        return horaireDepart;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    // ===== MÉTHODES Object =====

    @Override
    public String toString() {
        return String.format("%s → %s (%s) - %,.0f FCFA",
                villeDepart, villeArrivee, getHoraireDepartStr(), prixBase);
    }
}