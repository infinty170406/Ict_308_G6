package com.camrail.persistance.dao;

import com.camrail.core.model.Reservation;
import com.camrail.persistance.FileManager;
import com.camrail.persistance.exception.PersistanceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAccess implements TicketDAO {
    
    private static DataAccess instance;
    private List<Reservation> reservations;
    private boolean isInitialized = false;
    
    private DataAccess() {
        this.reservations = new ArrayList<>();
    }
    
    public static synchronized DataAccess getInstance() {
        if (instance == null) {
            instance = new DataAccess();
        }
        return instance;
    }
    
    /**
     * Charge toutes les réservations (appelé au démarrage)
     */
    private void init() {
        if (!isInitialized) {
            try {
                this.reservations = FileManager.chargerListe(
                    FileManager.getReservationsFilePath()
                );
                System.out.println("📂 " + reservations.size() + " réservation(s) chargée(s)");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("⚠️ Aucune donnée existante, démarrage à vide");
                this.reservations = new ArrayList<>();
            }
            isInitialized = true;
        }
    }
    
    /**
     * Sauvegarde une réservation
     */
    @Override
    public void sauvegarder(Reservation reservation) throws PersistanceException {
        try {
            init();
            
            // Vérifier si la réservation existe déjà
            boolean existe = reservations.stream()
                .anyMatch(r -> reservation.getNumeroTicket() != null && 
                              reservation.getNumeroTicket().equals(r.getNumeroTicket()));
            
            if (!existe) {
                reservations.add(reservation);
            }
            
            // Persister
            FileManager.sauvegarderListe(reservations, 
                FileManager.getReservationsFilePath());
            
            System.out.println("💾 Réservation sauvegardée : " + reservation.getNumeroTicket());
            
        } catch (IOException e) {
            throw new PersistanceException("Erreur lors de la sauvegarde", e);
        }
    }
    
    /**
     * Charge toutes les réservations
     */
    @Override
    public List<Reservation> chargerToutes() throws PersistanceException {
        try {
            init();
            return new ArrayList<>(reservations);
        } catch (Exception e) {
            throw new PersistanceException("Erreur lors du chargement", e);
        }
    }
    
    /**
     * Trouve une réservation par son numéro de ticket
     */
    @Override
    public Reservation trouverParNumero(String numeroTicket) throws PersistanceException {
        init();
        return reservations.stream()
            .filter(r -> numeroTicket.equals(r.getNumeroTicket()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Supprime une réservation
     */
    @Override
    public void supprimer(String numeroTicket) throws PersistanceException {
        try {
            init();
            reservations.removeIf(r -> numeroTicket.equals(r.getNumeroTicket()));
            FileManager.sauvegarderListe(reservations, 
                FileManager.getReservationsFilePath());
            System.out.println("🗑️ Réservation supprimée : " + numeroTicket);
        } catch (IOException e) {
            throw new PersistanceException("Erreur lors de la suppression", e);
        }
    }
    
    /**
     * Compte le nombre de réservations
     */
    public int compterReservations() {
        init();
        return reservations.size();
    }
}
