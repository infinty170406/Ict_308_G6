package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.components.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SelectionPanel extends JPanel {
    private MainFrame parent;
    private TrainTable trainTable;
    private SiegeGrid siegeGrid;
    private JComboBox<String> classCombo;
    private JButton btnNext;
    private JButton btnCancel;
    private JLabel trajetInfoLabel;
    private JLabel siegeInfoLabel;
    private JLabel pricePreviewLabel;
    
    private Object selectedTrajet;
    private Object selectedSiege;
    private String selectedClass;
    private int selectedRow = -1;
    
    public SelectionPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        
        initComponents();
        setupLayout();
        loadData();
    }
    
    private void initComponents() {
        // Tableau des trajets
        trainTable = new TrainTable();
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRow = trainTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedTrajet = trainTable.getTrajetAt(selectedRow);
                    trajetInfoLabel.setText("✅ " + selectedTrajet.toString());
                    trajetInfoLabel.setForeground(new Color(0, 153, 0));
                    updatePricePreview();
                    enableSiegeSelection(true);
                }
            }
        });
        
        // Grille des sièges
        siegeGrid = new SiegeGrid();
        siegeGrid.setListener(siegeNumber -> {
            selectedSiege = siegeGrid.getSiege(siegeNumber);
            siegeInfoLabel.setText("✅ Siège " + siegeNumber + " sélectionné");
            siegeInfoLabel.setForeground(new Color(0, 153, 0));
            updatePricePreview();
            btnNext.setEnabled(true);
        });
        siegeGrid.setEnabled(false);
        
        // Sélection de classe
        String[] classes = {"Économique", "Business", "Première"};
        classCombo = new JComboBox<>(classes);
        classCombo.addActionListener(e -> {
            selectedClass = (String) classCombo.getSelectedItem();
            updatePricePreview();
            if (selectedTrajet != null) {
                updateSiegeGrid();
            }
        });
        
        // Labels d'information
        trajetInfoLabel = new JLabel("Aucun trajet sélectionné");
        trajetInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        trajetInfoLabel.setForeground(Color.GRAY);
        
        siegeInfoLabel = new JLabel("Veuillez d'abord sélectionner un trajet");
        siegeInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        siegeInfoLabel.setForeground(Color.GRAY);
        
        pricePreviewLabel = new JLabel("Prix estimé: - CFA");
        pricePreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pricePreviewLabel.setForeground(new Color(0, 102, 204));
        
        // Boutons
        btnNext = new CustomButton("Continuer →", new Color(0, 102, 204));
        btnNext.setEnabled(false);
        btnCancel = new CustomButton("✕ Quitter", new Color(200, 50, 50));
        
        btnNext.addActionListener(e -> validateAndProceed());
        btnCancel.addActionListener(e -> confirmExit());
    }
    
    private void setupLayout() {
        // Nord - En-tête
        JPanel northPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        northPanel.setBackground(Color.WHITE);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));
        
        JLabel title = new JLabel("Choisissez votre trajet", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0, 51, 102));
        JLabel subtitle = new JLabel("Étape 1/3 - Sélectionnez votre destination et votre siège", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        
        northPanel.add(title);
        northPanel.add(subtitle);
        add(northPanel, BorderLayout.NORTH);
        
        // Centre - Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(450);
        splitPane.setBorder(null);
        
        // Gauche - Tableau
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(245, 247, 250));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        
        JScrollPane scrollPane = new JScrollPane(trainTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Trajets disponibles",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(0, 51, 102)
        ));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.add(new JLabel("Classe :"));
        filterPanel.add(classCombo);
        leftPanel.add(filterPanel, BorderLayout.SOUTH);
        
        // Droite - Grille
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(245, 247, 250));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBackground(Color.WHITE);
        gridContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        gridContainer.add(siegeGrid, BorderLayout.CENTER);
        
        // Légende
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendItem("🟢", "Disponible"));
        legendPanel.add(createLegendItem("🔴", "Occupé"));
        legendPanel.add(createLegendItem("🟡", "Sélectionné"));
        gridContainer.add(legendPanel, BorderLayout.SOUTH);
        
        rightPanel.add(gridContainer, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
        
        // Sud - Contrôles
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(createInfoRow("Trajet:", trajetInfoLabel));
        infoPanel.add(createInfoRow("Siège:", siegeInfoLabel));
        infoPanel.add(createInfoRow("Prix:", pricePreviewLabel));
        
        southPanel.add(infoPanel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnCancel);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnNext);
        
        southPanel.add(buttonPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoRow(String label, JComponent value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl);
        panel.add(value);
        return panel;
    }
    
    private JPanel createLegendItem(String color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        JLabel colorLabel = new JLabel(color);
        colorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(colorLabel);
        panel.add(textLabel);
        return panel;
    }
    
    private void loadData() {
        trainTable.loadTrajets();
    }
    
    private void updateSiegeGrid() {
        String classeKey = getClassKey(selectedClass);
        int totalPlaces = 0;
        if (selectedRow != -1) {
            totalPlaces = trainTable.getPlacesAt(selectedRow);
        }
        siegeGrid.updateForTrajet(totalPlaces, classeKey);
        siegeGrid.setEnabled(selectedTrajet != null);
        siegeInfoLabel.setText("Sélectionnez un siège disponible");
        siegeInfoLabel.setForeground(Color.GRAY);
        selectedSiege = null;
        btnNext.setEnabled(false);
    }
    
    private String getClassKey(String classe) {
        if (classe == null) return "ECONOMIQUE";
        if (classe.equals("Première")) return "PREMIERE";
        if (classe.equals("Business")) return "BUSINESS";
        return "ECONOMIQUE";
    }
    
    private void enableSiegeSelection(boolean enable) {
        if (enable) {
            updateSiegeGrid();
        } else {
            siegeGrid.setEnabled(false);
            siegeInfoLabel.setText("Veuillez d'abord sélectionner un trajet");
            siegeInfoLabel.setForeground(Color.GRAY);
            siegeGrid.resetSelection();
            selectedSiege = null;
            btnNext.setEnabled(false);
        }
    }
    
    private void updatePricePreview() {
        double price = 0;
        if (selectedRow != -1) {
            price = trainTable.getTrajetPrice(selectedRow);
            
            if (selectedClass != null) {
                if (selectedClass.equals("Première")) price += 3000;
                else if (selectedClass.equals("Business")) price += 1000;
            }
            
            if (selectedSiege != null) {
                SiegeGrid.SiegeInfo info = (SiegeGrid.SiegeInfo) selectedSiege;
                if ("VIP".equals(info.type)) price += 3000;
                else if ("PREMIUM".equals(info.type)) price += 1000;
            }
        }
        
        parent.setPrixTotal(price);
        pricePreviewLabel.setText("Prix estimé: " + String.format("%,.0f", price) + " CFA");
    }
    
    // ============================================
    // GESTION DES EXCEPTIONS - Écran 1
    // ============================================
    
    private void validateAndProceed() {
        // Exception 1 : Aucun trajet sélectionné
        if (selectedTrajet == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez sélectionner un trajet dans la liste avant de continuer.",
                "Erreur de sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Exception 2 : Aucun siège sélectionné
        if (selectedSiege == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez sélectionner un siège disponible avant de continuer.",
                "Erreur de sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Exception 8 : Trajet complet (vérification des places)
        if (selectedRow != -1) {
            int placesDisponibles = trainTable.getPlacesAt(selectedRow);
            if (placesDisponibles <= 0) {
                JOptionPane.showMessageDialog(this,
                    "❌ Désolé, ce trajet est complet. Veuillez choisir un autre trajet.",
                    "Trajet complet",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Tout est bon, on continue
        parent.setSelectedTrajet(selectedTrajet);
        parent.setSelectedSiege(selectedSiege);
        
        parent.showPanel("CONFIRMATION");
        parent.getConfirmationPanel().updateDisplay();
    }
    
    private void confirmExit() {
        // Exception 10 : Annulation par l'utilisateur
        int response = JOptionPane.showConfirmDialog(this,
            "⚠️ Êtes-vous sûr de vouloir quitter l'application ?\n" +
            "Votre réservation sera perdue.",
            "Confirmation de sortie",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public void resetSelection() {
        selectedTrajet = null;
        selectedSiege = null;
        selectedRow = -1;
        trajetInfoLabel.setText("Aucun trajet sélectionné");
        trajetInfoLabel.setForeground(Color.GRAY);
        siegeInfoLabel.setText("Veuillez d'abord sélectionner un trajet");
        siegeInfoLabel.setForeground(Color.GRAY);
        pricePreviewLabel.setText("Prix estimé: - CFA");
        btnNext.setEnabled(false);
        trainTable.clearSelection();
        siegeGrid.resetSelection();
        siegeGrid.setEnabled(false);
    }
}