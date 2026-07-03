package com.camrail.core.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Représente un siège dans un train.
 * Hérite de Produit et implémente IReservable pour la gestion des réservations.
 * Implémente Comparable pour le tri des sièges.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public class Siege extends Produit implements Serializable, Comparable<Siege>,
        IReservable {

    private static final long serialVersionUID = 1L;

    /** Numéro du siège */
    private int numero;

    /** Classe du siège (Première, Business, Économique) */
    private ClasseVoyage classe;

    /** Disponibilité du siège */
    private boolean disponible;

    /** Nom du passager qui a réservé (null si non réservé) */
    private String passagerReserve;

    /** Prix de base d'un siège */
    private static final double PRIX_BASE_SIEGE = 5000;

    /**
     * Constructeur
     * 
     * @param numero Numéro du siège
     * @param classe Classe du siège
     */
    public Siege(int numero, ClasseVoyage classe) {
        super("Siège " + numero, calculerPrixBase(classe));
        this.numero = numero;
        this.classe = classe;
        this.disponible = true;
        this.passagerReserve = null;
    }

    /**
     * Calcule le prix de base du siège selon sa classe
     * 
     * @param classe Classe du siège
     * @return Prix de base en FCFA
     */
    private static double calculerPrixBase(ClasseVoyage classe) {
        return classe.calculerPrix(PRIX_BASE_SIEGE);
    }

    // ===== IMPLÉMENTATION DE IReservable =====

    /**
     * Réserve le siège pour un passager
     * 
     * @param nomPassager Nom du passager
     * @throws IllegalStateException si le siège est déjà réservé
     */
    @Override
    public void reserver(String nomPassager) {
        if (!disponible) {
            throw new IllegalStateException(
                    "Le siège " + numero + " est déjà réservé par " + passagerReserve + " !");
        }
        if (nomPassager == null || nomPassager.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du passager est obligatoire !");
        }
        this.disponible = false;
        this.passagerReserve = nomPassager.trim();
    }

    /**
     * Libère le siège (annule la réservation)
     */
    @Override
    public void liberer() {
        this.disponible = true;
        this.passagerReserve = null;
    }

    /**
     * Vérifie si le siège est disponible
     * 
     * @return true si disponible, false si réservé
     */
    @Override
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Retourne le nom du passager qui a réservé
     * 
     * @return Nom du passager, ou null si non réservé
     */
    @Override
    public String getPassagerReserve() {
        return passagerReserve;
    }

    // ===== IMPLÉMENTATION DE Produit =====

    /**
     * Calcule le prix TTC du siège
     * 
     * @return Prix TTC en FCFA
     */
    @Override
    public double calculerPrixTTC() {
        return Math.round(prixBase * (1 + Trajet.TVA) * 100.0) / 100.0;
    }

    /**
     * Calcule la TVA du siège
     * 
     * @return Montant de la TVA en FCFA
     */
    @Override
    public double calculerTVA() {
        return Math.round(prixBase * Trajet.TVA * 100.0) / 100.0;
    }

    // ===== MÉTHODES MÉTIER SPÉCIFIQUES =====

    /**
     * Vérifie si le siège est d'une classe donnée
     * 
     * @param classe Classe à vérifier
     * @return true si le siège est de cette classe
     */
    public boolean estDeClasse(ClasseVoyage classe) {
        return this.classe == classe;
    }

    /**
     * Retourne le prix TTC du siège
     * 
     * @return Prix TTC en FCFA
     */
    public double getPrixAvecTVA() {
        return calculerPrixTTC();
    }

    /**
     * Retourne une description complète du siège
     * 
     * @return Description textuelle
     */
    public String getDescription() {
        String statut = disponible ? "✅ Disponible" : "❌ Réservé par " + passagerReserve;
        return String.format("Siège %d (%s) - %s",
                numero, classe.getLibelle(), statut);
    }

    // ===== GETTERS =====

    public int getNumero() {
        return numero;
    }

    public ClasseVoyage getClasse() {
        return classe;
    }

    // ===== MÉTHODES Object =====

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Siege siege = (Siege) o;
        return numero == siege.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return String.format("%s %d", classe.getIcone(), numero);
    }

    // ===== IMPLÉMENTATION Comparable =====

    /**
     * Compare les sièges par classe puis par numéro
     * 
     * @param autre Autre siège à comparer
     * @return < 0 si this est avant, 0 si égal, > 0 si this est après
     */
    @Override
    public int compareTo(Siege autre) {
        // D'abord comparer par classe (Première > Business > Économique)
        int classCompare = this.classe.compareTo(autre.classe);
        if (classCompare != 0)
            return classCompare;
        // Ensuite par numéro
        return Integer.compare(this.numero, autre.numero);
    }
}
