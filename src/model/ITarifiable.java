package model;

/**
 * Interface pour les objets qui ont un prix et des taxes.
 * Toute entité tarifiable doit pouvoir calculer son prix
 * avec et sans taxes.
 * 
 * Utile pour : Trajet, Siege, Produit, Service, etc.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public interface ITarifiable {

    /**
     * Calcule le prix TTC (Toutes Taxes Comprises)
     * 
     * @return Prix TTC en FCFA
     */
    double calculerPrixTTC();

    /**
     * Calcule le prix HT (Hors Taxes)
     * 
     * @return Prix HT en FCFA
     */
    double calculerPrixHT();

    /**
     * Calcule le montant de la TVA
     * 
     * @return Montant de la TVA en FCFA
     */
    double calculerTVA();

    /**
     * Retourne le prix de base (avant taxes et coefficients)
     * 
     * @return Prix de base en FCFA
     */
    double getPrixBase();
}