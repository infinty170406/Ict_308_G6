package com.camrail.ihm.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SiegeGrid extends JPanel {
    private JToggleButton[][] siegeButtons;
    private int rows = 8;
    private int cols = 4;
    private SiegeSelectionListener listener;
    private List<Integer> occupiedSieges;
    private int selectedSiegeNumber = -1;
    private List<SiegeInfo> siegeInfos;
    private int totalSieges = 32;
    
    public interface SiegeSelectionListener {
        void onSiegeSelected(int siegeNumber);
    }
    
    public SiegeGrid() {
        siegeInfos = new ArrayList<>();
        occupiedSieges = new ArrayList<>();
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new GridLayout(rows, cols, 10, 10));
        buildGrid(rows, cols);
    }
    
    /**
     * Construire la grille avec un nombre spécifique de sièges
     */
    public void buildGrid(int totalSieges) {
        this.totalSieges = totalSieges;
        
        // Déterminer le nombre de colonnes
        int cols = 4;
        if (totalSieges > 40) cols = 5;
        if (totalSieges > 60) cols = 6;
        if (totalSieges > 80) cols = 7;
        
        // Calculer le nombre de rangées nécessaires
        int rows = (int) Math.ceil((double) totalSieges / cols);
        
        // S'assurer d'avoir au moins 4 rangées pour l'esthétique
        if (rows < 4) rows = 4;
        
        this.rows = rows;
        this.cols = cols;
        
        buildGrid(rows, cols);
    }
    
    private void buildGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.siegeButtons = new JToggleButton[rows][cols];
        this.siegeInfos = new ArrayList<>();
        this.occupiedSieges = new ArrayList<>();
        
        removeAll();
        setLayout(new GridLayout(rows, cols, 10, 10));
        
        int numero = 1;
        Random random = new Random();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JToggleButton btn = new JToggleButton(String.valueOf(numero));
                btn.setPreferredSize(new Dimension(60, 50));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setBackground(new Color(46, 204, 113));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
                
                // Déterminer le type de siège selon la rangée
                String type = "CLASSIQUE";
                if (i < 2) type = "VIP";
                else if (i < 4) type = "PREMIUM";
                
                // Simuler quelques sièges occupés (20%)
                boolean occupe = random.nextInt(10) < 2;
                if (occupe && numero <= totalSieges) {
                    btn.setBackground(new Color(231, 76, 60));
                    btn.setEnabled(false);
                    occupiedSieges.add(numero);
                }
                
                // Masquer les sièges qui dépassent le total
                if (numero > totalSieges) {
                    btn.setVisible(false);
                    btn.setEnabled(false);
                }
                
                siegeInfos.add(new SiegeInfo(numero, type, occupe, i, j));
                
                final int siegeNumero = numero;
                btn.addActionListener(e -> handleSiegeSelection(btn, siegeNumero));
                
                siegeButtons[i][j] = btn;
                add(btn);
                numero++;
            }
        }
        
        revalidate();
        repaint();
    }
    
    private void handleSiegeSelection(JToggleButton btn, int numero) {
        if (btn.isSelected()) {
            // Désélectionner tous les autres
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (siegeButtons[i][j] != btn && siegeButtons[i][j].isVisible()) {
                        siegeButtons[i][j].setSelected(false);
                    }
                }
            }
            selectedSiegeNumber = numero;
            if (listener != null) {
                listener.onSiegeSelected(numero);
            }
        } else {
            selectedSiegeNumber = -1;
        }
    }
    
    public void updateSieges(String classe) {
        resetSelection();
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < siegeInfos.size()) {
                    SiegeInfo info = siegeInfos.get(index);
                    JToggleButton btn = siegeButtons[i][j];
                    
                    // Vérifier si le siège correspond à la classe
                    boolean correspond = false;
                    if ("ECONOMIQUE".equals(classe)) {
                        correspond = "CLASSIQUE".equals(info.type);
                    } else if ("BUSINESS".equals(classe)) {
                        correspond = "CLASSIQUE".equals(info.type) || "PREMIUM".equals(info.type);
                    } else if ("PREMIERE".equals(classe)) {
                        correspond = true;
                    }
                    
                    // Si le siège dépasse le total, le masquer
                    if (info.numero > totalSieges) {
                        btn.setVisible(false);
                        btn.setEnabled(false);
                    } else if (correspond && !info.occupe) {
                        btn.setBackground(new Color(46, 204, 113)); // Vert disponible
                        btn.setEnabled(true);
                        btn.setVisible(true);
                    } else if (info.occupe) {
                        btn.setBackground(new Color(231, 76, 60)); // Rouge occupé
                        btn.setEnabled(false);
                        btn.setVisible(true);
                    } else {
                        btn.setBackground(new Color(200, 200, 200)); // Gris non disponible
                        btn.setEnabled(false);
                        btn.setVisible(true);
                    }
                    index++;
                }
            }
        }
    }
    
    public void updateForTrajet(int totalPlaces, String classe) {
        buildGrid(totalPlaces);
        updateSieges(classe);
    }
    
    public Object getSiege(int numero) {
        for (SiegeInfo info : siegeInfos) {
            if (info.numero == numero) {
                return info;
            }
        }
        return null;
    }
    
    public void setListener(SiegeSelectionListener listener) {
        this.listener = listener;
    }
    
    public void resetSelection() {
        selectedSiegeNumber = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (siegeButtons[i][j] != null) {
                    siegeButtons[i][j].setSelected(false);
                }
            }
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            resetSelection();
        }
    }
    
    // Classe interne pour stocker les infos des sièges
    public class SiegeInfo {
        public int numero;
        public String type;
        public boolean occupe;
        public int row;
        public int col;
        
        public SiegeInfo(int numero, String type, boolean occupe, int row, int col) {
            this.numero = numero;
            this.type = type;
            this.occupe = occupe;
            this.row = row;
            this.col = col;
        }
        
        @Override
        public String toString() {
            return "Siège " + numero + " (" + type + ")" + (occupe ? " - Occupé" : " - Disponible");
        }
    }
}