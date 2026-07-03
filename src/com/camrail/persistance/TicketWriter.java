package com.camrail.persistance;

import com.camrail.core.model.Reservation;
import com.camrail.persistance.exception.TicketException;
import java.io.*;

public class TicketWriter {
    
    private static final String TICKET_DIR = "tickets/";
    
    /**
     * Génère un fichier ticket pour une réservation
     * @return le numéro du ticket généré
     */
    public static String genererTicket(Reservation reservation) 
            throws TicketException {
        
        // 1. Créer le dossier tickets/ s'il n'existe pas
        File dossier = new File(TICKET_DIR);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
        
        // 2. Récupérer le numéro unique
        String numeroTicket = reservation.getNumeroTicket();
        
        // 3. Récupérer le contenu du ticket formaté
        String contenu = reservation.getTicketFormate();
        
        // 4. Écrire dans le fichier
        String nomFichier = TICKET_DIR + "ticket_" + numeroTicket + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier))) {
            writer.print(contenu);
        } catch (IOException e) {
            throw new TicketException("Erreur lors de la génération du ticket", e);
        }
        
        return numeroTicket;
    }
}
