package com.camrail;

import com.camrail.ihm.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Appliquer le Look and Feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("=== Lancement de Camrail Intercity ===");
        
        // Lancer l'application sur l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}