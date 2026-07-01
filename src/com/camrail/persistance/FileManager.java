package persistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    
    private static final String DATA_DIR = "data/";
    private static final String RESERVATIONS_FILE = DATA_DIR + "reservations.ser";
    
    static {
        // Créer le dossier data/ au chargement
        File dossier = new File(DATA_DIR);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
    }
    
    /**
     * Sauvegarde une liste d'objets sérialisables
     */
    public static <T> void sauvegarderListe(List<T> objets, String fichier) 
            throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fichier))) {
            oos.writeObject(objets);
        }
    }
    
    /**
     * Charge une liste d'objets sérialisables
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> chargerListe(String fichier) 
            throws IOException, ClassNotFoundException {
        
        File file = new File(fichier);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        }
    }
    
    /**
     * Vérifie si un fichier est accessible en écriture
     */
    public static boolean estAccessible(String chemin) {
        File fichier = new File(chemin);
        try {
            if (!fichier.exists()) {
                return fichier.createNewFile();
            }
            return fichier.canWrite();
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Récupère le chemin du fichier de réservations
     */
    public static String getReservationsFilePath() {
        return RESERVATIONS_FILE;
    }
}