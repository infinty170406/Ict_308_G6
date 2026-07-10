package com.camrail.ihm.javafx;

import com.camrail.core.model.ClasseVoyage;
import com.camrail.core.model.Siege;
import com.camrail.core.model.Trajet;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStoreFX {
    public static class City {
        public String name;
        public String code;
        public String img;
        public String desc;
        public double minPrice;

        public City(String name, String code, String img, String desc, double minPrice) {
            this.name = name;
            this.code = code;
            this.img = img;
            this.desc = desc;
            this.minPrice = minPrice;
        }
    }

    public static class Promo {
        public String tag;
        public String title;
        public String desc;
        public String code;

        public Promo(String tag, String title, String desc, String code) {
            this.tag = tag;
            this.title = title;
            this.desc = desc;
            this.code = code;
        }
    }

    public static class Review {
        public String name;
        public int rating;
        public String text;

        public Review(String name, int rating, String text) {
            this.name = name;
            this.rating = rating;
            this.text = text;
        }
    }

    public static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id;
        public String name;
        public String prenom;
        public String phone;
        public String email;
        public String password;
        public boolean isAdmin;

        public User(String id, String name, String prenom, String phone, String email, String password, boolean isAdmin) {
            this.id = id;
            this.name = name;
            this.prenom = prenom;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.isAdmin = isAdmin;
        }
    }

    public static class ReservationFX implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id;
        public String userId;
        public String trainCode;
        public String depart;
        public String arrivee;
        public String date;
        public String heure;
        public String classe;
        public int siegeNum;
        public String nomPassager;
        public String prenomPassager;
        public double prixTotal;
        public String status; // "Confirmé", "Annulé"
        public String modePaiement;

        public ReservationFX(String id, String userId, String trainCode, String depart, String arrivee, String date, String heure, String classe, int siegeNum, String nomPassager, String prenomPassager, double prixTotal, String status, String modePaiement) {
            this.id = id;
            this.userId = userId;
            this.trainCode = trainCode;
            this.depart = depart;
            this.arrivee = arrivee;
            this.date = date;
            this.heure = heure;
            this.classe = classe;
            this.siegeNum = siegeNum;
            this.nomPassager = nomPassager;
            this.prenomPassager = prenomPassager;
            this.prixTotal = prixTotal;
            this.status = status;
            this.modePaiement = modePaiement;
        }
    }

    public static List<City> cities = new ArrayList<>();
    public static List<Promo> promos = new ArrayList<>();
    public static List<Review> reviews = new ArrayList<>();
    
    public static List<Trajet> trains = new ArrayList<>();
    public static List<User> users = new ArrayList<>();
    public static List<ReservationFX> reservations = new ArrayList<>();

    static {
        // Initialiser les Villes Populaires
        cities.add(new City("Douala", "DLA", "", "La capitale économique dynamique en bordure de Wouri.", 5000));
        cities.add(new City("Yaoundé", "YDE", "", "La capitale politique verdoyante aux sept collines.", 5000));
        cities.add(new City("Garoua", "GUA", "", "Le port fluvial chaleureux du Septentrion.", 8000));
        cities.add(new City("Kribi", "KRI", "", "La cité balnéaire aux plages de sable blanc.", 6000));
        cities.add(new City("Ngaoundéré", "NGE", "", "Le carrefour ferroviaire du château d'eau.", 7000));

        // Initialiser les Promos
        promos.add(new Promo("Saison Étudiante", "Tarif Jeune -25%", "Profitez de réductions immédiates pour tous les étudiants du Cameroun.", "JEUNE25"));
        promos.add(new Promo("Famille Nombreuse", "Offre Tribu -15%", "Voyagez en groupe ou en famille nombreuse et économisez sur chaque billet.", "TRIBU15"));
        promos.add(new Promo("Week-end Plage", "Spécial Kribi -10%", "Fuyez le stress urbain à prix réduit le week-end avec Express Kribi.", "BEACH10"));

        // Initialiser les Avis
        reviews.add(new Review("Marc Alen", 5, "Un voyage fantastique entre Yaoundé et Douala. Le train était extrêmement moderne, le service impeccable et la réservation en ligne rapide !"));
        reviews.add(new Review("Sandrine N.", 4, "Très satisfaite de la première classe. Calme, climatisation parfaite et prise pour ordinateur portable fonctionnelle. Je recommande."));
        reviews.add(new Review("Dr. Jean-Pierre", 5, "Le dashboard admin me permet de suivre toutes les réservations facilement. Une modélisation très professionnelle digne d'une grande entreprise !"));

        // Initialiser les Trains
        trains.add(new Trajet("Yaoundé", "Douala", "06:30", 18000, 135));
        trains.add(new Trajet("Douala", "Yaoundé", "09:00", 18000, 135));
        trains.add(new Trajet("Yaoundé", "Ngaoundéré", "20:00", 32000, 660));
        trains.add(new Trajet("Ngaoundéré", "Yaoundé", "19:30", 32000, 645));
        trains.add(new Trajet("Douala", "Bafoussam", "08:15", 16000, 225));
        trains.add(new Trajet("Bafoussam", "Douala", "14:30", 16000, 225));
        trains.add(new Trajet("Yaoundé", "Kribi", "07:30", 12000, 180));
        trains.add(new Trajet("Kribi", "Yaoundé", "16:00", 12000, 180));

        // Charger ou initialiser les utilisateurs et les réservations persistantes
        loadUsers();
        loadReservations();
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        File file = new File("camrail_users.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (List<User>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // S'assurer qu'au moins l'admin existe
        boolean adminExists = users.stream().anyMatch(u -> u.isAdmin);
        if (!adminExists) {
            users.add(new User("usr-admin", "Administrateur", "Camrail", "+237 600000000", "admin@camrail.cm", "admin", true));
            users.add(new User("usr-101", "Kamdem", "Arthur", "+237 677889900", "arthur@gmail.com", "pass123", false));
            saveUsers();
        }
    }

    public static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("camrail_users.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadReservations() {
        File file = new File("camrail_res.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                reservations = (List<ReservationFX>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Ajouter quelques fausses réservations de départ pour meubler l'admin
        if (reservations.isEmpty()) {
            reservations.add(new ReservationFX("RES-928374", "usr-101", "TRA-YDE-DLA-1", "Yaoundé", "Douala", "2026-07-10", "08:00", "BUSINESS", 12, "Kamdem", "Arthur", 6000.0, "Confirmé", "Orange Money"));
            reservations.add(new ReservationFX("RES-123456", "usr-101", "TRA-YDE-DLA-2", "Yaoundé", "Douala", "2026-07-11", "14:00", "ECONOMIQUE", 25, "Kamdem", "Arthur", 5000.0, "Confirmé", "MTN Mobile Money"));
            saveReservations();
        }
    }

    public static void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("camrail_res.dat"))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
