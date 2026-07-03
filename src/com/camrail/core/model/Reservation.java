package com.camrail.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Représente une réservation complète d'un billet de train.
 * Implémente ITicketable pour la génération de tickets.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public class Reservation implements ITicketable, Serializable {

    private static final long serialVersionUID = 1L;

    /** Numéro unique du ticket (format: CAR-YYYYMMDD-XXXX) */
    private String numeroTicket;

    /** Trajet réservé */
    private Trajet trajet;

    /** Siège réservé */
    private Siege siege;

    /** Nom du passager */
    private String nomPassager;

    /** Prénom du passager */
    private String prenomPassager;

    /** Téléphone du passager (optionnel) */
    private String telephone;

    /** Date et heure de la réservation */
    private LocalDateTime dateReservation;

    /** Statut de paiement */
    private boolean paye;

    /** Date et heure du paiement (null si non payé) */
    private LocalDateTime datePaiement;

    /** Formateur de date pour l'affichage */
    private static final DateTimeFormatter FORMAT_DATE_TICKET = DateTimeFormatter.ofPattern("dd MMMM yyyy à HH:mm");

    /**
     * Constructeur principal
     * 
     * @param trajet         Trajet réservé
     * @param siege          Siège réservé
     * @param nomPassager    Nom du passager
     * @param prenomPassager Prénom du passager
     */
    public Reservation(Trajet trajet, Siege siege, String nomPassager, String prenomPassager) {
        if (trajet == null) {
            throw new IllegalArgumentException("Le trajet est obligatoire !");
        }
        if (siege == null) {
            throw new IllegalArgumentException("Le siège est obligatoire !");
        }
        if (nomPassager == null || nomPassager.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du passager est obligatoire !");
        }

        this.trajet = trajet;
        this.siege = siege;
        this.nomPassager = nomPassager.trim();
        this.prenomPassager = (prenomPassager != null) ? prenomPassager.trim() : "";
        this.dateReservation = LocalDateTime.now();
        this.paye = false;
        this.numeroTicket = genererNumeroTicket();
        this.telephone = null;
    }

    /**
     * Constructeur simplifié (nom complet uniquement)
     * 
     * @param trajet     Trajet réservé
     * @param siege      Siège réservé
     * @param nomComplet Nom complet du passager
     */
    public Reservation(Trajet trajet, Siege siege, String nomComplet) {
        this(trajet, siege, nomComplet, "");
    }

    /**
     * Génère un numéro de ticket unique
     * Format: CAR-YYYYMMDD-XXXX (ex: CAR-20260702-A7F3)
     * 
     * @return Numéro de ticket
     */
    private String genererNumeroTicket() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();
        return "CAR-" + date + "-" + random;
    }

    /**
     * Valide le paiement de la réservation
     * 
     * @throws IllegalStateException si déjà payée
     */
    public void validerPaiement() {
        if (this.paye) {
            throw new IllegalStateException(
                    "Cette réservation a déjà été payée !");
        }
        this.paye = true;
        this.datePaiement = LocalDateTime.now();
        // Utiliser l'interface IReservable pour réserver le siège
        this.siege.reserver(nomPassager + " " + prenomPassager);
    }

    // ===== IMPLÉMENTATION DE ITicketable =====

    /**
     * Calcule le prix total TTC du billet
     * 
     * @return Prix total en FCFA
     */
    @Override
    public double getPrixTotal() {
        return trajet.calculerPrixAvecClasse(siege.getClasse());
    }

    /**
     * Retourne le numéro unique du ticket
     * 
     * @return Numéro de ticket
     */
    @Override
    public String getNumeroTicket() {
        return numeroTicket;
    }

    /**
     * Retourne une description formatée du ticket
     * 
     * @return Description textuelle
     */
    @Override
    public String getDescriptionTicket() {
        return String.format(
                "🎫 Billet %s\n" +
                        "🚉 %s → %s\n" +
                        "👤 %s %s\n" +
                        "💺 Siège %d (%s)\n" +
                        "💰 %,.0f FCFA",
                numeroTicket,
                trajet.getVilleDepart(),
                trajet.getVilleArrivee(),
                prenomPassager,
                nomPassager,
                siege.getNumero(),
                siege.getClasse().getLibelle(),
                getPrixTotal());
    }

    // ===== MÉTHODES MÉTIER =====

    /**
     * Calcule le prix HT du billet
     * 
     * @return Prix HT en FCFA
     */
    public double getPrixHT() {
        return trajet.calculerPrixHT(siege.getClasse());
    }

    /**
     * Calcule le montant de la TVA du billet
     * 
     * @return Montant de la TVA en FCFA
     */
    public double getMontantTVA() {
        return trajet.calculerTVA(siege.getClasse());
    }

    /**
     * Retourne le ticket formaté pour impression
     * 
     * @return Ticket formaté en texte brut
     */
    public String getTicketFormate() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("        🌍 CARNAIL INTERCITY - BILLET DE TRAIN        \n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("\n");
        sb.append("  📅 DATE       : ").append(dateReservation.format(FORMAT_DATE_TICKET)).append("\n");
        sb.append("  🎫 N° TICKET  : ").append(numeroTicket).append("\n");
        sb.append("  👤 PASSAGER   : ").append(prenomPassager).append(" ").append(nomPassager).append("\n");
        if (telephone != null && !telephone.isEmpty()) {
            sb.append("  📞 TÉLÉPHONE  : ").append(telephone).append("\n");
        }
        sb.append("\n");
        sb.append("  🚉 TRAJET     : ").append(trajet.getVilleDepart())
                .append(" → ").append(trajet.getVilleArrivee()).append("\n");
        sb.append("  ⏰ DÉPART     : ").append(trajet.getHoraireDepartStr()).append("\n");
        sb.append("  🕐 ARRIVÉE    : ").append(trajet.getHeureArrivee()
                .format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
        sb.append("  ⏱️ DURÉE      : ").append(trajet.getDureeFormatee()).append("\n");
        sb.append("\n");
        sb.append("  💺 SIÈGE      : ").append(siege.getNumero()).append("\n");
        sb.append("  🏷️ CLASSE     : ").append(siege.getClasse().getLibelle()).append("\n");
        sb.append("\n");
        sb.append("  ──────────────────────────────────────────────\n");
        sb.append("  💰 PRIX HT    : ").append(String.format("%,.0f", getPrixHT())).append(" FCFA\n");
        sb.append("  📊 TVA (19,25%) : ").append(String.format("%,.0f", getMontantTVA())).append(" FCFA\n");
        sb.append("  💳 PRIX TOTAL : ").append(String.format("%,.0f", getPrixTotal())).append(" FCFA\n");
        sb.append("  ✅ STATUT     : ").append(paye ? "PAYÉ ✅" : "NON PAYÉ ❌").append("\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("          MERCI ET BON VOYAGE ! 🚄\n");
        sb.append("=".repeat(50)).append("\n");
        return sb.toString();
    }

    /**
     * Retourne la date de réservation formatée
     * 
     * @return Date formatée "dd/MM/yyyy HH:mm"
     */
    public String getDateReservationFormatee() {
        return dateReservation.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // ===== GETTERS ET SETTERS =====

    public Trajet getTrajet() {
        return trajet;
    }

    public Siege getSiege() {
        return siege;
    }

    public String getNomPassager() {
        return nomPassager;
    }

    public String getPrenomPassager() {
        return prenomPassager;
    }

    public String getNomComplet() {
        return prenomPassager + " " + nomPassager;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public boolean isPaye() {
        return paye;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setNomPassager(String nomPassager) {
        if (nomPassager != null && !nomPassager.trim().isEmpty()) {
            this.nomPassager = nomPassager.trim();
        }
    }

    public void setPrenomPassager(String prenomPassager) {
        if (prenomPassager != null) {
            this.prenomPassager = prenomPassager.trim();
        }
    }

    // ===== MÉTHODES Object =====

    @Override
    public String toString() {
        return String.format("🎫 %s - %s %s - %s → %s - %,.0f FCFA",
                numeroTicket,
                prenomPassager, nomPassager,
                trajet.getVilleDepart(),
                trajet.getVilleArrivee(),
                getPrixTotal());
    }
}
