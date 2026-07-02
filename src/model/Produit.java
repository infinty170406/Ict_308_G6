package model;

import java.io.Serializable;

/**
 * Classe abstraite représentant un produit vendu par Carnail Intercity.
 * Sert de base pour l'héritage (Trajet, Siege, etc.)
 * 
 * Une classe abstraite ne peut pas être instanciée directement.
 * Elle définit un contrat partiel : des attributs communs et
 * des méthodes abstraites que les sous-classes doivent implémenter.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public abstract class Produit implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nom du produit */
    protected String nom;

    /** Prix de base (avant taxes et coefficients) */
    protected double prixBase;

    /**
     * Constructeur de la classe abstraite
     * 
     * @param nom      Nom du produit
     * @param prixBase Prix de base en FCFA
     */
    public Produit(String nom, double prixBase) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide !");
        }
        if (prixBase < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif !");
        }
        this.nom = nom;
        this.prixBase = prixBase;
    }

    /**
     * Calcule le prix TTC du produit
     * Méthode abstraite : les sous-classes doivent l'implémenter
     * 
     * @return Prix TTC en FCFA
     */
    public abstract double calculerPrixTTC();

    /**
     * Calcule la TVA du produit
     * Méthode abstraite : les sous-classes doivent l'implémenter
     * 
     * @return Montant de la TVA en FCFA
     */
    public abstract double calculerTVA();

    // ===== GETTERS =====

    /**
     * Retourne le nom du produit
     * 
     * @return Nom du produit
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le prix de base
     * 
     * @return Prix de base en FCFA
     */
    public double getPrixBase() {
        return prixBase;
    }

    // ===== MÉTHODES Object =====

    @Override
    public String toString() {
        return nom + " (" + String.format("%,.0f", prixBase) + " FCFA)";
    }
}