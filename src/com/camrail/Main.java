package com.camrail;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.BorderLayout;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Initialisation du Projet Camrail Intercity ===");
        
        // Lance une fenêtre de test Swing simple pour confirmer le bon fonctionnement de l'IHM
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Camrail Intercity - Initialisation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            JLabel label = new JLabel("Projet Camrail Intercity Initialisé !", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 18));
            frame.add(label, BorderLayout.CENTER);

            JLabel subLabel = new JLabel("Prêt pour le développement de l'examen ICT308.", SwingConstants.CENTER);
            subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            frame.add(subLabel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}
