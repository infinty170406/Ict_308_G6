package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.components.CustomButton;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ConfirmationPanel extends JPanel {
    private MainFrame parent;
    private JTextField nomField;
    private JTextField cniField;
    private JTextArea recapArea;
    private JButton btnBack;
    private JButton btnConfirm;
    private JButton btnCancel;
    private JLabel prixLabel;
    
    public ConfirmationPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        // Champs passager
        nomField = new JTextField(20);
        nomField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cniField = new JTextField(15);
        cniField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Zone de récapitulatif
        recapArea = new JTextArea();
        recapArea.setEditable(false);
        recapArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        recapArea.setBackground(new Color(250, 250, 250));
        recapArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Prix
        prixLabel = new JLabel("", JLabel.CENTER);
        prixLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        prixLabel.setForeground(new Color(0, 153, 0));
        
        // Boutons
        btnBack = new CustomButton("← Retour", new Color(100, 100, 100));
        btnConfirm = new CustomButton("💳 Confirmer et payer", new Color(0, 153, 0));
        btnCancel = new CustomButton("✕ Annuler", new Color(200, 50, 50));
        
        btnBack.addActionListener(e -> parent.showPanel("SELECTION"));
        btnConfirm.addActionListener(e -> validateAndConfirm());
        btnCancel.addActionListener(e -> confirmExit());
    }
    
    private void setupLayout() {
        // Nord - En-tête
        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.setBackground(Color.WHITE);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));
        
        JLabel title = new JLabel("Confirmez votre réservation", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0, 51, 102));
        JLabel subtitle = new JLabel("Étape 2/3 - Saisissez vos informations et vérifiez le récapitulatif", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        
        northPanel.add(title);
        northPanel.add(subtitle);
        add(northPanel, BorderLayout.NORTH);
        
        // Centre - Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerLocation(350);
        splitPane.setBorder(null);
        
        // Panneau gauche - Formulaire passager
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Icône
        JLabel iconLabel = new JLabel("👤", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftPanel.add(iconLabel, gbc);
        
        // Nom
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        leftPanel.add(new JLabel("Nom complet:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(nomField, gbc);
        
        // CNI
        gbc.gridy = 2;
        gbc.gridx = 0;
        leftPanel.add(new JLabel("N° CNI:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(cniField, gbc);
        
        // Espace
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        leftPanel.add(Box.createVerticalStrut(20), gbc);
        
        // Prix total
        gbc.gridy = 4;
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(new Color(240, 248, 255));
        pricePanel.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)));
        pricePanel.add(prixLabel, BorderLayout.CENTER);
        leftPanel.add(pricePanel, gbc);
        
        // Panneau droit - Récapitulatif
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(245, 247, 250));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(recapArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Récapitulatif de votre réservation",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(0, 51, 102)
        ));
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
        
        // Sud - Boutons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));
        
        southPanel.add(btnBack);
        southPanel.add(btnCancel);
        southPanel.add(btnConfirm);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    public void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("           RÉCAPITULATIF DE VOTRE RÉSERVATION\n");
        sb.append("================================================\n\n");
        
        Object trajet = parent.getSelectedTrajet();
        if (trajet != null) {
            sb.append("📍 TRAJET:\n");
            sb.append("   ").append(trajet.toString()).append("\n\n");
        }
        
        Object siege = parent.getSelectedSiege();
        if (siege != null) {
            sb.append("💺 SIÈGE:\n");
            sb.append("   ").append(siege.toString()).append("\n\n");
        }
        
        double prix = parent.getPrixTotal();
        sb.append("💰 PRIX:\n");
        sb.append("   Total: ").append(String.format("%,.0f", prix)).append(" CFA\n");
        sb.append("   Taxes (15%): ").append(String.format("%,.0f", prix * 0.15)).append(" CFA\n");
        sb.append("   TOTAL À PAYER: ").append(String.format("%,.0f", prix * 1.15)).append(" CFA\n");
        sb.append("\n");
        
        sb.append("------------------------------------------------\n");
        sb.append("📝 Veuillez saisir vos informations ci-contre\n");
        sb.append("   pour finaliser votre réservation.\n");
        sb.append("================================================\n");
        
        recapArea.setText(sb.toString());
        prixLabel.setText("Total: " + String.format("%,.0f", prix) + " CFA");
        
        nomField.setText("");
        cniField.setText("");
    }
    
    // ============================================
    // GESTION DES EXCEPTIONS - Écran 2
    // ============================================
    
    private void validateAndConfirm() {
        String nom = nomField.getText().trim();
        String cni = cniField.getText().trim();
        
        // Exception 3 : Nom vide
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez saisir votre nom complet. Ce champ est obligatoire.",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            nomField.requestFocus();
            nomField.setBackground(new Color(255, 240, 240));
            return;
        }
        nomField.setBackground(Color.WHITE);
        
        // Exception 4 : Nom trop court
        if (nom.length() < 2) {
            JOptionPane.showMessageDialog(this,
                "❌ Le nom doit contenir au moins 2 caractères.",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            nomField.requestFocus();
            nomField.setBackground(new Color(255, 240, 240));
            return;
        }
        nomField.setBackground(Color.WHITE);
        
        // Exception 5 : CNI vide
        if (cni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez saisir votre numéro de CNI. Ce champ est obligatoire.",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            cniField.requestFocus();
            cniField.setBackground(new Color(255, 240, 240));
            return;
        }
        cniField.setBackground(Color.WHITE);
        
        // Exception 6 : CNI trop courte
        if (cni.length() < 5) {
            JOptionPane.showMessageDialog(this,
                "❌ Le numéro de CNI doit contenir au moins 5 caractères.",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            cniField.requestFocus();
            cniField.setBackground(new Color(255, 240, 240));
            return;
        }
        cniField.setBackground(Color.WHITE);
        
        // Exception 9 : CNI invalide (caractères spéciaux)
        if (!cni.matches("^[A-Za-z0-9]+$")) {
            JOptionPane.showMessageDialog(this,
                "❌ Le numéro de CNI ne doit contenir que des lettres et des chiffres.\n" +
                "Exemple: CNI12345678",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            cniField.requestFocus();
            cniField.setBackground(new Color(255, 240, 240));
            return;
        }
        cniField.setBackground(Color.WHITE);
        
        // Exception 11 : Paiement refusé (simulation 5% de chance)
        if (Math.random() < 0.05) {
            JOptionPane.showMessageDialog(this,
                "❌ Le paiement a été refusé.\n\n" +
                "Raison: Fonds insuffisants ou carte bancaire invalide.\n" +
                "Veuillez vérifier vos informations bancaires et réessayer.",
                "Paiement refusé",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tout est bon !
        parent.setNomPassager(nom);
        parent.setCniPassager(cni);
        
        String ticketNumber = genererTicketId();
        String codeSecurite = genererCodeSecurite();
        parent.setTicketNumber(ticketNumber);
        parent.setCodeSecurite(codeSecurite);
        
        parent.showPanel("IMPRESSION");
        parent.getImpressionPanel().startImpression();
    }
    
    private String genererTicketId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String random = String.format("%06d", new Random().nextInt(999999));
        return "CAM-" + date + "-" + random;
    }
    
    private String genererCodeSecurite() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    private void confirmExit() {
        // Exception 10 : Annulation par l'utilisateur
        int response = JOptionPane.showConfirmDialog(this,
            "⚠️ Êtes-vous sûr de vouloir annuler votre réservation ?\n" +
            "Toutes vos sélections seront perdues.",
            "Confirmation d'annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            parent.resetAll();
        }
    }
}