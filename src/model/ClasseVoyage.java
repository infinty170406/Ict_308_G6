package model;

/**
 * Énumération des classes de voyage avec leurs coefficients de prix.
 * Chaque classe a :
 * - Un libellé (nom affichable)
 * - Un coefficient multiplicateur pour le prix
 * - Une icône pour l'affichage
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public enum ClasseVoyage {

    /** 1ère Classe - Premium avec coefficient 2.5 */
    PREMIERE("1ère Classe", 2.5, "🟡"),

    /** Classe Business - Confort avec coefficient 1.8 */
    BUSINESS("Business", 1.8, "🔵"),

    /** Classe Économique - Standard avec coefficient 1.0 */
    ECONOMIQUE("Économique", 1.0, "🟢");

    /** Libellé affichable de la classe */
    private final String libelle;

    /** Coefficient multiplicateur pour le prix */
    private final double coefficientPrix;

    /** Icône pour l'affichage */
    private final String icone;

    /**
     * Constructeur de l'énumération
     * 
     * @param libelle         Libellé de la classe
     * @param coefficientPrix Coefficient multiplicateur
     * @param icone           Icône d'affichage
     */
    ClasseVoyage(String libelle, double coefficientPrix, String icone) {
        this.libelle = libelle;
        this.coefficientPrix = coefficientPrix;
        this.icone = icone;
    }

    // ===== GETTERS =====

    /**
     * Retourne le libellé de la classe
     * 
     * @return Libellé (ex: "1ère Classe")
     */
    public String getLibelle() {
        return libelle;
    }

    /**
     * Retourne le coefficient multiplicateur
     * 
     * @return Coefficient (ex: 2.5 pour Première)
     */
    public double getCoefficientPrix() {
        return coefficientPrix;
    }

    /**
     * Retourne l'icône de la classe
     * 
     * @return Icône (ex: "🟡")
     */
    public String getIcone() {
        return icone;
    }

    // ===== MÉTHODES MÉTIER =====

    /**
     * Calcule le prix selon la classe à partir d'un prix de base
     * 
     * @param prixBase Prix de base en FCFA
     * @return Prix avec coefficient appliqué
     */
    public double calculerPrix(double prixBase) {
        return Math.round(prixBase * coefficientPrix * 100.0) / 100.0;
    }

    // ===== MÉTHODES Object =====

    @Override
    public String toString() {
        return icone + " " + libelle + " (x" + coefficientPrix + ")";
    }
}