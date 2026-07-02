package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.Theme;
import com.camrail.ihm.components.*;
import javax.swing.*;
import java.awt.*;

public class SelectionPanel extends JPanel {
    private MainFrame parent;
    private TrainTable trainTable;
    private SiegeGrid siegeGrid;
    private RailNetworkMap networkMap;
    private JourneyTimeline journeyTimeline;
    private JComboBox<String> classCombo;
    private JButton btnNext;
    private JButton btnCancel;
    private JLabel trajetInfoLabel;
    private JLabel siegeInfoLabel;
    private JLabel pricePreviewLabel;
    
    private TrainTable.TrajetInfo selectedTrajet;
    private Object selectedSiege;
    private String selectedClass = "Économique";
    private int selectedRow = -1;
    
    public SelectionPanel(MainFrame parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        
        initComponents();
        setupLayout();
        loadData();
    }
    
    private void initComponents() {
        // Network Map
        networkMap = new RailNetworkMap();
        
        // Journey Timeline
        journeyTimeline = new JourneyTimeline();

        // Cards list of trains
        trainTable = new TrainTable();
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRow = trainTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedTrajet = trainTable.getTrajetAt(selectedRow);
                    trajetInfoLabel.setText("LIAISON: " + selectedTrajet.getDepart() + " ➔ " + selectedTrajet.getArrivee() + " (" + selectedTrajet.getHeure() + ")");
                    trajetInfoLabel.setForeground(Theme.NEON_CYAN);
                    updatePricePreview();
                    enableSiegeSelection(true);
                    
                    // Update components
                    networkMap.setActiveRoute(selectedTrajet.getDepart(), selectedTrajet.getArrivee());
                    journeyTimeline.setRoute(selectedTrajet.getDepart(), selectedTrajet.getArrivee(), selectedTrajet.getDuree());
                }
            }
        });
        
        // Custom Wagon seat selection
        siegeGrid = new SiegeGrid();
        siegeGrid.setListener(siegeNumber -> {
            selectedSiege = siegeGrid.getSiege(siegeNumber);
            siegeInfoLabel.setText("SIÈGE N° " + siegeNumber + " CONFIRMÉ");
            siegeInfoLabel.setForeground(Theme.NEON_CYAN);
            updatePricePreview();
            btnNext.setEnabled(true);
        });
        siegeGrid.setEnabled(false);
        
        // ComboBox with futuristic styling
        String[] classes = {"Économique", "Business", "Première"};
        classCombo = new JComboBox<>(classes);
        classCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        classCombo.setBackground(Theme.BG_DARK);
        classCombo.setForeground(Theme.TEXT_LIGHT);
        classCombo.addActionListener(e -> {
            selectedClass = (String) classCombo.getSelectedItem();
            updatePricePreview();
            if (selectedTrajet != null) {
                updateSiegeGrid();
            }
        });
        
        // Information Labels
        trajetInfoLabel = new JLabel("VEUILLEZ SÉLECTIONNER UN TRAJET DANS LA LISTE");
        trajetInfoLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        trajetInfoLabel.setForeground(Theme.TEXT_MUTED);
        
        siegeInfoLabel = new JLabel("EN ATTENTE DE SÉLECTION DE TRAJET...");
        siegeInfoLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        siegeInfoLabel.setForeground(Theme.TEXT_MUTED);
        
        pricePreviewLabel = new JLabel("PRIX TRANSIT: - CFA");
        pricePreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pricePreviewLabel.setForeground(Theme.NEON_PINK);
        
        // Actions
        btnNext = new CustomButton("CONTINUER ➔", Theme.NEON_BLUE);
        btnNext.setEnabled(false);
        btnCancel = new CustomButton("✕ RETOUR", Theme.NEON_PINK);
        
        btnNext.addActionListener(e -> validateAndProceed());
        btnCancel.addActionListener(e -> parent.showPanel("WELCOME"));
    }
    
    private void setupLayout() {
        // --- 1. NORTH HEADER PANEL ---
        JPanel northPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 16);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 16, Theme.NEON_CYAN, Theme.NEON_PURPLE);
                g2.dispose();
            }
        };
        northPanel.setOpaque(false);
        northPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel title = new JLabel("PROTOCOLE DE RÉSERVATION DE VOYAGE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT_LIGHT);
        
        JLabel subtitle = new JLabel("STAGE 01 // SELECTION DU TRAJET & PLAN DE WAGON INTERACTIF");
        subtitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        subtitle.setForeground(Theme.NEON_CYAN);
        
        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(subtitle, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        
        // --- 2. CENTER PANEL (Split Left/Right) ---
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);
        
        // A. Left Side: Network Map & Card List
        JPanel leftPanel = new JPanel(new BorderLayout(10, 12));
        leftPanel.setOpaque(false);
        
        // Add map at the top
        leftPanel.add(networkMap, BorderLayout.NORTH);
        
        // Container for scrollable cards
        JPanel cardsContainer = new JPanel(new BorderLayout(10, 10));
        cardsContainer.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(trainTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        cardsContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Filter subpanel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 12);
                g2.dispose();
            }
        };
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        JLabel filterLbl = new JLabel("CLASSE D'ACCÈS :");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLbl.setForeground(Theme.TEXT_LIGHT);
        filterPanel.add(filterLbl);
        filterPanel.add(classCombo);
        
        cardsContainer.add(filterPanel, BorderLayout.SOUTH);
        leftPanel.add(cardsContainer, BorderLayout.CENTER);
        
        // B. Right Side: Interactive Seat Map
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 20);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 20, Theme.NEON_CYAN, Theme.NEON_BLUE);
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        rightPanel.add(siegeGrid, BorderLayout.CENTER);
        
        // Legend below the wagon seat map
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 2));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem("🟢", "Disponible"));
        legendPanel.add(createLegendItem("🔴", "Occupé"));
        legendPanel.add(createLegendItem("🟡", "Sélection"));
        legendPanel.add(createLegendItem("🔵", "VIP / Premium"));
        rightPanel.add(legendPanel, BorderLayout.SOUTH);
        
        centerGrid.add(leftPanel);
        centerGrid.add(rightPanel);
        add(centerGrid, BorderLayout.CENTER);
        
        // --- 3. SOUTH PANEL (DOCK SCREEN) ---
        JPanel southContainer = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 20);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 20, Theme.NEON_PURPLE, Theme.NEON_CYAN);
                g2.dispose();
            }
        };
        southContainer.setOpaque(false);
        southContainer.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        // Timeline in the center of the dock
        southContainer.add(journeyTimeline, BorderLayout.CENTER);
        
        // Left side texts
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(trajetInfoLabel);
        infoPanel.add(siegeInfoLabel);
        infoPanel.add(pricePreviewLabel);
        southContainer.add(infoPanel, BorderLayout.WEST);
        
        // Right side buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnNext);
        southContainer.add(buttonPanel, BorderLayout.EAST);
        
        add(southContainer, BorderLayout.SOUTH);
    }
    
    private JPanel createLegendItem(String emoji, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        textLbl.setForeground(Theme.TEXT_MUTED);
        
        panel.add(emojiLbl);
        panel.add(textLbl);
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
        siegeInfoLabel.setText("VEUILLEZ CHOISIR UN SIÈGE DISPONIBLE");
        siegeInfoLabel.setForeground(Theme.TEXT_LIGHT);
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
            siegeInfoLabel.setText("EN ATTENTE DE SÉLECTION DE TRAJET...");
            siegeInfoLabel.setForeground(Theme.TEXT_MUTED);
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
        pricePreviewLabel.setText("PRIX TRANSIT: " + String.format("%,.0f", price) + " CFA");
    }
    
    private void validateAndProceed() {
        if (selectedTrajet == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez sélectionner un trajet avant de continuer.",
                "Erreur de sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedSiege == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez sélectionner un siège dans le wagon.",
                "Erreur de sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        parent.setSelectedTrajet(selectedTrajet);
        parent.setSelectedSiege(selectedSiege);
        
        parent.showPanel("CONFIRMATION");
        parent.getConfirmationPanel().updateDisplay();
    }
    
    public void resetSelection() {
        selectedTrajet = null;
        selectedSiege = null;
        selectedRow = -1;
        trajetInfoLabel.setText("VEUILLEZ SÉLECTIONNER UN TRAJET DANS LA LISTE");
        trajetInfoLabel.setForeground(Theme.TEXT_MUTED);
        siegeInfoLabel.setText("EN ATTENTE DE SÉLECTION DE TRAJET...");
        siegeInfoLabel.setForeground(Theme.TEXT_MUTED);
        pricePreviewLabel.setText("PRIX TRANSIT: - CFA");
        btnNext.setEnabled(false);
        trainTable.clearSelection();
        siegeGrid.resetSelection();
        siegeGrid.setEnabled(false);
        networkMap.setActiveRoute("", "");
        // timeline is updated dynamically
    }
}