package com.camrail.ihm;

import com.camrail.ihm.panels.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
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
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 700));
        
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);
        
        // Création des panneaux
        selectionPanel = new SelectionPanel(this);
        confirmationPanel = new ConfirmationPanel(this);
        impressionPanel = new ImpressionPanel(this);
        
        // Ajout au CardLayout
        mainPanel.add(selectionPanel, "SELECTION");
        mainPanel.add(confirmationPanel, "CONFIRMATION");
        mainPanel.add(impressionPanel, "IMPRESSION");
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // Barre de statut
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Camrail Intercity v2.0 - Assistant de réservation de billets");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        updateTitle(panelName);
    }
    
    private void updateTitle(String panelName) {
        switch(panelName) {
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
        showPanel("SELECTION");
    }
}