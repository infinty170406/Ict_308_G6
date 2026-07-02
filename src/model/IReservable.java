package model;

/**
 * Interface pour les objets qui peuvent être réservés.
 * Toute entité réservable doit pouvoir être réservée, libérée,
 * et fournir des informations sur sa disponibilité.
 * 
 * Utile pour : Siege, Chambre, Place, etc.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public interface IReservable {

    /**
     * Réserve l'objet pour un passager
     * 
     * @param nomPassager Nom complet du passager qui réserve
     * @throws IllegalStateException si l'objet est déjà réservé
     */
    void reserver(String nomPassager);

    /**
     * Libère l'objet (annule la réservation)
     */
    void liberer();

    /**
     * Vérifie si l'objet est disponible
     * 
     * @return true si disponible, false si déjà réservé
     */
    boolean isDisponible();

    /**
     * Retourne le nom du passager qui a réservé
     * 
     * @return Nom du passager, ou null si non réservé
     */
    String getPassagerReserve();
}