package persistance;

import core.modele.Reservation;
import java.util.List;

public interface TicketDAO {
    
    /**
     * Sauvegarde une réservation (fichier sérialisé OU base de données)
     */
    void sauvegarder(Reservation reservation) throws PersistanceException;
    
    /**
     * Charge toutes les réservations sauvegardées
     */
    List<Reservation> chargerToutes() throws PersistanceException;
    
    /**
     * Trouve une réservation par son numéro de ticket
     */
    Reservation trouverParNumero(String numeroTicket) throws PersistanceException;
    
    /**
     * Supprime une réservation
     */
    void supprimer(String numeroTicket) throws PersistanceException;
}