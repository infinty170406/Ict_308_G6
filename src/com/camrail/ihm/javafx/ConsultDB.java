package com.camrail.ihm.javafx;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class ConsultDB {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      CONSULTATION DE LA BASE DE DONNÉES CAMRAIL   ");
        System.out.println("==================================================\n");

        consultUsers();
        System.out.println();
        consultReservations();
    }

    @SuppressWarnings("unchecked")
    private static void consultUsers() {
        System.out.println(">>> UTILISATEURS ENREGISTRÉS (camrail_users.dat) :");
        File file = new File("camrail_users.dat");
        if (!file.exists()) {
            System.out.println("Aucune base d'utilisateurs trouvée (fichier inexistant).");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<DataStoreFX.User> users = (List<DataStoreFX.User>) ois.readObject();
            System.out.printf("%-12s | %-20s | %-15s | %-25s | %-12s | %-5s\n", 
                "ID", "Nom & Prénom", "Téléphone", "Email", "Mot de passe", "Admin");
            System.out.println("------------------------------------------------------------------------------------------------------");
            for (DataStoreFX.User u : users) {
                System.out.printf("%-12s | %-20s | %-15s | %-25s | %-12s | %-5s\n",
                    u.id, u.name + " " + u.prenom, u.phone, u.email, u.password, u.isAdmin ? "OUI" : "NON");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture de la base d'utilisateurs : " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void consultReservations() {
        System.out.println(">>> RÉSERVATIONS EFFECTUÉES (camrail_res.dat) :");
        File file = new File("camrail_res.dat");
        if (!file.exists()) {
            System.out.println("Aucune base de réservations trouvée (fichier inexistant).");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<DataStoreFX.ReservationFX> resList = (List<DataStoreFX.ReservationFX>) ois.readObject();
            System.out.printf("%-14s | %-10s | %-22s | %-10s | %-6s | %-10s | %-6s | %-18s | %-10s | %-10s\n",
                "ID Rés.", "User ID", "Trajet", "Date", "Heure", "Classe", "Siège", "Passager", "Prix", "Statut");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------");
            for (DataStoreFX.ReservationFX r : resList) {
                String trajet = r.depart + " -> " + r.arrivee;
                System.out.printf("%-14s | %-10s | %-22s | %-10s | %-6s | %-10s | %-6d | %-18s | %-10.0f | %-10s\n",
                    r.id, r.userId, trajet, r.date, r.heure, r.classe, r.siegeNum, r.nomPassager + " " + r.prenomPassager, r.prixTotal, r.status);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture de la base de réservations : " + e.getMessage());
        }
    }
}
