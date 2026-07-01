package persistance;

import core.modele.Reservation;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import util.GenerateurCode;

public class TicketWriter {
    
    private static final String TICKET_DIR = "tickets/";
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    
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
        
        // 2. Générer un numéro unique
        String numeroTicket = GenerateurCode.genererNumeroTicket();
        
        // 3. Créer le contenu du ticket
        String contenu = formaterTicket(reservation, numeroTicket);
        
        // 4. Écrire dans le fichier
        String nomFichier = TICKET_DIR + "ticket_" + numeroTicket + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier))) {
            writer.print(contenu);
        } catch (IOException e) {
            throw new TicketException("Erreur lors de la génération du ticket", e);
        }
        
        return numeroTicket;
    }
    
    private static String formaterTicket(Reservation res, String num) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         CAMRAIL INTERCITY\n");
        sb.append("         BILLET DE TRAIN\n");
        sb.append("========================================\n");
        sb.append("Numéro : ").append(num).append("\n");
        sb.append("Date   : ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        sb.append("\n");
        sb.append("Trajet : ").append(res.getTrajet()).append("\n");
        sb.append("Classe : ").append(res.getClasse()).append("\n");
        sb.append("Siège  : ").append(res.getSiege()).append("\n");
        sb.append("Voyageur: ").append(res.getVoyageur().getNom()).append("\n");
        sb.append("----------------------------------------\n");
        
        double prixBase = res.getPrix();
        double taxes = prixBase * 0.18;
        double total = prixBase + taxes;
        
        sb.append(String.format("Prix HT : %.0f FCFA\n", prixBase));
        sb.append(String.format("Taxes (18%%) : %.0f FCFA\n", taxes));
        sb.append(String.format("Total TTC : %.0f FCFA\n", total));
        sb.append("========================================\n");
        sb.append("Code sécurité : ").append(genererCodeSecurite()).append("\n");
        sb.append("========================================\n");
        sb.append("     MERCI DE VOYAGER AVEC CAMRAIL\n");
        sb.append("========================================\n");
        return sb.toString();
    }
    
    private static String genererCodeSecurite() {
        // Génère un code style AZE-789-XYZ
        return GenerateurCode.genererCodeSecurite();
    }
}