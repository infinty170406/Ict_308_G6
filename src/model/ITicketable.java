package model;

/**
 * Interface pour les objets qui peuvent être imprimés sous forme de ticket.
 * Toute entité ticketable doit fournir les informations nécessaires
 * pour générer un ticket d'achat ou de réservation.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public interface ITicketable {

    /**
     * Retourne le prix total TTC du ticket
     * 
     * @return Prix total en FCFA
     */
    double getPrixTotal();

    /**
     * Retourne le numéro unique du ticket
     * 
     * @return Numéro de ticket au format CAR-YYYYMMDD-XXXX
     */
    String getNumeroTicket();

    /**
     * Retourne une description formatée du ticket pour l'affichage
     * 
     * @return Description textuelle du ticket
     */
    String getDescriptionTicket();
}