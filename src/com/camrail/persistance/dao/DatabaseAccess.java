package com.camrail.persistance.dao;

import com.camrail.core.model.*;
import com.camrail.persistance.exception.PersistanceException;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess implements TicketDAO {
    
    private static DatabaseAccess instance;
    private static final String DB_URL = "jdbc:sqlite:data/camrail.db";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private DatabaseAccess() {
        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        initTable();
    }
    
    public static synchronized DatabaseAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }
    
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite introuvable", e);
        }
        return DriverManager.getConnection(DB_URL);
    }
    
    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
                     " numero_ticket TEXT PRIMARY KEY," +
                     " nom_passager TEXT NOT NULL," +
                     " prenom_passager TEXT," +
                     " telephone TEXT," +
                     " trajet_depart TEXT NOT NULL," +
                     " trajet_arrivee TEXT NOT NULL," +
                     " trajet_horaire TEXT NOT NULL," +
                     " trajet_prix_base REAL NOT NULL," +
                     " trajet_duree INTEGER NOT NULL," +
                     " siege_numero INTEGER NOT NULL," +
                     " siege_classe TEXT NOT NULL," +
                     " date_reservation TEXT NOT NULL," +
                     " paye INTEGER NOT NULL," +
                     " date_paiement TEXT" +
                     ");";
                     
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("💾 Table SQLite 'reservations' initialisée avec succès.");
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible d'initialiser la base de données : " + e.getMessage());
        }
    }
    
    @Override
    public void sauvegarder(Reservation r) throws PersistanceException {
        String sql = "INSERT OR REPLACE INTO reservations (" +
                     " numero_ticket, nom_passager, prenom_passager, telephone," +
                     " trajet_depart, trajet_arrivee, trajet_horaire, trajet_prix_base, trajet_duree," +
                     " siege_numero, siege_classe, date_reservation, paye, date_paiement" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                     
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, r.getNumeroTicket());
            pstmt.setString(2, r.getNomPassager());
            pstmt.setString(3, r.getPrenomPassager());
            pstmt.setString(4, r.getTelephone());
            
            Trajet t = r.getTrajet();
            pstmt.setString(5, t.getVilleDepart());
            pstmt.setString(6, t.getVilleArrivee());
            pstmt.setString(7, t.getHoraireDepartStr());
            pstmt.setDouble(8, t.getPrixBase());
            pstmt.setInt(9, t.getDureeMinutes());
            
            Siege s = r.getSiege();
            pstmt.setInt(10, s.getNumero());
            pstmt.setString(11, s.getClasse().name());
            
            pstmt.setString(12, r.getDateReservation().format(DATE_FORMATTER));
            pstmt.setInt(13, r.isPaye() ? 1 : 0);
            pstmt.setString(14, r.getDatePaiement() != null ? r.getDatePaiement().format(DATE_FORMATTER) : null);
            
            pstmt.executeUpdate();
            System.out.println("💾 Réservation sauvegardée dans la BD SQLite : " + r.getNumeroTicket());
            
        } catch (SQLException e) {
            throw new PersistanceException("Erreur de sauvegarde en base de données", e);
        }
    }
    
    @Override
    public List<Reservation> chargerToutes() throws PersistanceException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations;";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
            
        } catch (SQLException e) {
            throw new PersistanceException("Erreur de chargement depuis la base de données", e);
        }
        return list;
    }
    
    @Override
    public Reservation trouverParNumero(String numeroTicket) throws PersistanceException {
        String sql = "SELECT * FROM reservations WHERE numero_ticket = ?;";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroTicket);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new PersistanceException("Erreur de recherche en base de données", e);
        }
        return null;
    }
    
    @Override
    public void supprimer(String numeroTicket) throws PersistanceException {
        String sql = "DELETE FROM reservations WHERE numero_ticket = ?;";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroTicket);
            pstmt.executeUpdate();
            System.out.println("🗑️ Réservation supprimée de la BD SQLite : " + numeroTicket);
            
        } catch (SQLException e) {
            throw new PersistanceException("Erreur de suppression en base de données", e);
        }
    }
    
    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        String depart = rs.getString("trajet_depart");
        String arrivee = rs.getString("trajet_arrivee");
        String horaire = rs.getString("trajet_horaire");
        double prixBase = rs.getDouble("trajet_prix_base");
        int duree = rs.getInt("trajet_duree");
        
        Trajet trajet = new Trajet(depart, arrivee, horaire, prixBase, duree);
        
        int siegeNum = rs.getInt("siege_numero");
        ClasseVoyage classe = ClasseVoyage.valueOf(rs.getString("siege_classe"));
        Siege siege = new Siege(siegeNum, classe);
        
        String nom = rs.getString("nom_passager");
        String prenom = rs.getString("prenom_passager");
        
        Reservation r = new Reservation(trajet, siege, nom, prenom);
        r.setTelephone(rs.getString("telephone"));
        
        boolean paye = rs.getInt("paye") == 1;
        if (paye) {
            try {
                java.lang.reflect.Field payeField = Reservation.class.getDeclaredField("paye");
                payeField.setAccessible(true);
                payeField.set(r, true);
                
                String datePaiementStr = rs.getString("date_paiement");
                if (datePaiementStr != null) {
                    java.lang.reflect.Field datePaiementField = Reservation.class.getDeclaredField("datePaiement");
                    datePaiementField.setAccessible(true);
                    datePaiementField.set(r, LocalDateTime.parse(datePaiementStr, DATE_FORMATTER));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        String dateResStr = rs.getString("date_reservation");
        if (dateResStr != null) {
            try {
                java.lang.reflect.Field dateResField = Reservation.class.getDeclaredField("dateReservation");
                dateResField.setAccessible(true);
                dateResField.set(r, LocalDateTime.parse(dateResStr, DATE_FORMATTER));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return r;
    }
}
