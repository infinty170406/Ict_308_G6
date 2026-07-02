package com.camrail.ihm;

import com.camrail.ihm.panels.*;
import com.camrail.ihm.components.FuturisticBackground;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private WelcomePanel welcomePanel;
    private SelectionPanel selectionPanel;
    private ConfirmationPanel confirmationPanel;
    private ImpressionPanel impressionPanel;
    
    // Données partagées entre les écrans
    private Object selectedTrajet;
    private Object selectedSiege;
    private String nomPassager;
    private String cniPassager;
    private double prixTotal;
    private String ticketNumber;
    private String codeSecurite;
    
    public MainFrame() {
        setTitle("Camrail Intercity - Système de Réservation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 750));
        
        initComponents();
        setupUI();
        showPanel("WELCOME");
    }
    
    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);
        
        // Création des panneaux
        welcomePanel = new WelcomePanel(this);
        selectionPanel = new SelectionPanel(this);
        confirmationPanel = new ConfirmationPanel(this);
        impressionPanel = new ImpressionPanel(this);
        
        // Rendre les sous-panneaux transparents pour laisser voir l'arrière-plan animé
        selectionPanel.setOpaque(false);
        confirmationPanel.setOpaque(false);
        impressionPanel.setOpaque(false);
        
        // Ajout au CardLayout
        mainPanel.add(welcomePanel, "WELCOME");
        mainPanel.add(selectionPanel, "SELECTION");
        mainPanel.add(confirmationPanel, "CONFIRMATION");
        mainPanel.add(impressionPanel, "IMPRESSION");
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Utiliser le fond d'écran futuriste animé
        FuturisticBackground background = new FuturisticBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);
        
        background.add(mainPanel, BorderLayout.CENTER);
        
        // Barre de statut en verre
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(15, 15, 30, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0, 240, 255, 40));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 25));
        
        JLabel statusLabel = new JLabel("SYSTEM STATUS: ONLINE // TRANSIT PROTOCOL ACTIVE // SECURE LINK ESTABLISHED");
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        statusLabel.setForeground(Theme.NEON_CYAN);
        statusPanel.add(statusLabel);
        
        background.add(statusPanel, BorderLayout.SOUTH);
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        updateTitle(panelName);
    }
    
    private void updateTitle(String panelName) {
        switch(panelName) {
            case "WELCOME":
                setTitle("Camrail Intercity - Terminal de Réservation");
                break;
            case "SELECTION":
                setTitle("Camrail - Étape 1/3 : Choix du trajet et du siège");
                break;
            case "CONFIRMATION":
                setTitle("Camrail - Étape 2/3 : Confirmation et paiement");
                break;
            case "IMPRESSION":
                setTitle("Camrail - Étape 3/3 : Impression du billet");
                break;
        }
    }
    
    // Getters & Setters
    public SelectionPanel getSelectionPanel() { return selectionPanel; }
    public ConfirmationPanel getConfirmationPanel() { return confirmationPanel; }
    public ImpressionPanel getImpressionPanel() { return impressionPanel; }
    
    public Object getSelectedTrajet() { return selectedTrajet; }
    public void setSelectedTrajet(Object trajet) { this.selectedTrajet = trajet; }
    
    public Object getSelectedSiege() { return selectedSiege; }
    public void setSelectedSiege(Object siege) { this.selectedSiege = siege; }
    
    public String getNomPassager() { return nomPassager; }
    public void setNomPassager(String nom) { this.nomPassager = nom; }
    
    public String getCniPassager() { return cniPassager; }
    public void setCniPassager(String cni) { this.cniPassager = cni; }
    
    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prix) { this.prixTotal = prix; }
    
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    
    public String getCodeSecurite() { return codeSecurite; }
    public void setCodeSecurite(String codeSecurite) { this.codeSecurite = codeSecurite; }
    
    public void resetAll() {
        selectedTrajet = null;
        selectedSiege = null;
        nomPassager = null;
        cniPassager = null;
        prixTotal = 0;
        ticketNumber = null;
        codeSecurite = null;
        selectionPanel.resetSelection();
        showPanel("WELCOME");
    }
}