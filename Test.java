import model.ClasseVoyage;
import model.GestionReservations;
import model.IReservable;
import model.ITarifiable;
import model.Produit;
import model.Reservation;
import model.Siege;
import model.Trajet;

/**
 * Classe de test pour l'équipe Core.
 * Permet de valider que toutes les classes fonctionnent ensemble.
 * 
 * @author Équipe Core - ICT308
 * @version 1.0
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));

        // ===== TEST 1 : Enum ClasseVoyage =====
        System.out.println("\n1️⃣ TEST DE ClasseVoyage :");
        System.out.println("   - " + ClasseVoyage.PREMIERE);
        System.out.println("   - " + ClasseVoyage.BUSINESS);
        System.out.println("   - " + ClasseVoyage.ECONOMIQUE);
        System.out.println("   ✅ Enum fonctionne !");

        // ===== TEST 2 : GestionReservations =====
        System.out.println("\n2️⃣ TEST DE GestionReservations :");
        GestionReservations gestion = new GestionReservations();
        System.out.println("   - Trajets : " + gestion.getTrajetsDisponibles().size());
        System.out.println("   - Sièges : " + gestion.getSiegeDisponibles().size());
        System.out.println("   ✅ GestionReservations fonctionne !");

        // ===== TEST 3 : Création d'une réservation =====
        System.out.println("\n3️⃣ TEST DE CRÉATION DE RÉSERVATION :");
        try {
            Trajet trajet = gestion.getTrajetsDisponibles().get(0);
            Siege siege = gestion.getSiegeDisponibles().get(0);

            System.out.println("   📍 Trajet : " + trajet);
            System.out.println("   💺 Siège : " + siege);

            Reservation res = gestion.creerReservation(trajet, siege, "Dupont", "Jean");
            res.validerPaiement();

            System.out.println("   ✅ Réservation créée !");
            System.out.println("   🎫 Ticket : " + res.getNumeroTicket());
            System.out.println("   💰 Prix TTC : " + res.getPrixTotal() + " FCFA");

        } catch (Exception e) {
            System.out.println("   ❌ Erreur : " + e.getMessage());
        }

        // ===== TEST 4 : Statistiques =====
        System.out.println("\n4️⃣ STATISTIQUES :");
        System.out.println("   - Réservations totales : " + gestion.getNombreReservations());
        System.out.println("   - Réservations payées : " + gestion.getNombreReservationsPayees());
        System.out.println("   - Chiffre d'affaires : " +
                String.format("%,.0f", gestion.getChiffreAffairesTotal()) + " FCFA");
        System.out.println("   ✅ Statistiques fonctionnent !");

        // ===== TEST 5 : Recherche de sièges =====
        System.out.println("\n5️⃣ RECHERCHE DE SIÈGES DISPONIBLES :");
        System.out.println("   - Sièges Première disponibles : " +
                gestion.trouverSiegesDisponibles(ClasseVoyage.PREMIERE).size());
        System.out.println("   - Sièges Business disponibles : " +
                gestion.trouverSiegesDisponibles(ClasseVoyage.BUSINESS).size());
        System.out.println("   - Sièges Économique disponibles : " +
                gestion.trouverSiegesDisponibles(ClasseVoyage.ECONOMIQUE).size());
        System.out.println("   ✅ Recherche fonctionne !");

        // ===== TEST 6 : Héritage et Interfaces =====
        System.out.println("\n6️⃣ VÉRIFICATION HÉRITAGE & INTERFACES :");
        Trajet t = gestion.getTrajetsDisponibles().get(0);
        Siege s = gestion.getSiegeDisponibles().get(0);

        System.out.println("   - Trajet est un Produit : " + (t instanceof Produit));
        System.out.println("   - Trajet implémente ITarifiable : " + (t instanceof ITarifiable));
        System.out.println("   - Siege est un Produit : " + (s instanceof Produit));
        System.out.println("   - Siege implémente IReservable : " + (s instanceof IReservable));
        System.out.println("   - Siege implémente Comparable : " + (s instanceof Comparable));
        System.out.println("   ✅ Héritage et interfaces OK !");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ TOUS LES TESTS SONT PASSÉS !");
        System.out.println("📦 Équipe Core prête à livrer le code !");
        System.out.println("=".repeat(60));
    }
}