package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.Theme;
import com.camrail.ihm.components.CustomButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ConfirmationPanel extends JPanel {
    private MainFrame parent;
    private JTextField nomField;
    private JTextField cniField;
    private HolographicRecap recapPanel;
    private QRCodePanel qrCodePanel;
    private JButton btnBack;
    private JButton btnConfirm;
    private JButton btnCancel;
    private JLabel prixLabel;
    
    public ConfirmationPanel(MainFrame parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        nomField = createCyberTextField();
        cniField = createCyberTextField();
        
        qrCodePanel = new QRCodePanel();
        recapPanel = new HolographicRecap();
        
        prixLabel = new JLabel("", JLabel.CENTER);
        prixLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        prixLabel.setForeground(Theme.NEON_PINK);
        
        btnBack = new CustomButton("← RETOUR", new Color(100, 100, 115));
        btnConfirm = new CustomButton("💳 TRANSACT ET PAYER", Theme.NEON_BLUE);
        btnCancel = new CustomButton("✕ ANNULER", Theme.NEON_PINK);
        
        btnBack.addActionListener(e -> parent.showPanel("SELECTION"));
        btnConfirm.addActionListener(e -> validateAndConfirm());
        btnCancel.addActionListener(e -> confirmExit());
    }
    
    private JTextField createCyberTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.BOLD, 15));
        field.setBackground(new Color(12, 12, 24));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Theme.NEON_CYAN);
        field.setPreferredSize(new Dimension(280, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 240, 255, 60), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.NEON_CYAN, 2),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 240, 255, 60), 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
        });
        return field;
    }
    
    private void setupLayout() {
        // North Header
        JPanel northPanel = new JPanel(new BorderLayout()) {
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
        
        JLabel title = new JLabel("AUTHENTIFICATION DE RÉSERVATION");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT_LIGHT);
        
        JLabel subtitle = new JLabel("STAGE 02 // VÉRIFICATION DES BIOMÉTRIQUES & CRÉDITS");
        subtitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        subtitle.setForeground(Theme.NEON_CYAN);
        
        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(subtitle, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        
        // Center Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setResizeWeight(0.42);
        splitPane.setDividerLocation(420);
        splitPane.setBorder(null);
        
        // Left Panel - Passenger Biometrics inputs (Stacked Layout)
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 20);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 20, Theme.NEON_CYAN, Theme.NEON_BLUE);
                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Large Biometric Fingerprint/Identity icon
        JLabel iconLabel = new JLabel("👤", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        iconLabel.setForeground(Theme.NEON_CYAN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(iconLabel, gbc);
        
        // Form Title
        JLabel formTitle = new JLabel("ENREGISTREMENT PASSAGER", JLabel.CENTER);
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Theme.NEON_CYAN);
        gbc.gridy = 1;
        leftPanel.add(formTitle, gbc);
        
        // Vertical spacer
        gbc.gridy = 2;
        leftPanel.add(Box.createVerticalStrut(10), gbc);
        
        // Nom Field
        JLabel nomLbl = new JLabel("👤 NOM COMPLET DU PASSAGER");
        nomLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nomLbl.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 3;
        leftPanel.add(nomLbl, gbc);
        
        gbc.gridy = 4;
        leftPanel.add(nomField, gbc);
        
        // CNI Field
        JLabel cniLbl = new JLabel("🪪 NUMÉRO D'IDENTIFICATION CNI");
        cniLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cniLbl.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 5;
        leftPanel.add(cniLbl, gbc);
        
        gbc.gridy = 6;
        leftPanel.add(cniField, gbc);
        
        // Vertical spacer
        gbc.gridy = 7;
        leftPanel.add(Box.createVerticalStrut(15), gbc);
        
        // Price Badge Card inside left pane
        JPanel priceContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 12);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 12, Theme.NEON_PINK, Theme.NEON_PURPLE);
                g2.dispose();
            }
        };
        priceContainer.setOpaque(false);
        priceContainer.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        priceContainer.add(prixLabel, BorderLayout.CENTER);
        
        gbc.gridy = 8;
        leftPanel.add(priceContainer, gbc);
        
        // Right Panel - Holographic Ticket Boarding Pass
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 20);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 20, Theme.NEON_PURPLE, Theme.NEON_CYAN);
                
                // Holographic design lines
                g2.setColor(new Color(0, 240, 255, 15));
                g2.setStroke(Theme.STROKE_1);
                for (int y = 30; y < getHeight(); y += 45) {
                    g2.drawLine(20, y, getWidth() - 20, y);
                }
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Place QR Code and details together in right pane
        JPanel ticketContent = new JPanel(new BorderLayout(15, 0));
        ticketContent.setOpaque(false);
        ticketContent.add(recapPanel, BorderLayout.CENTER);
        
        // Wrap QR Panel in container to align it nicely
        JPanel qrContainer = new JPanel(new BorderLayout(0, 8));
        qrContainer.setOpaque(false);
        qrContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JLabel qrTitle = new JLabel("ACCÈS SECURISE QR", JLabel.CENTER);
        qrTitle.setFont(new Font("Monospaced", Font.BOLD, 10));
        qrTitle.setForeground(Theme.NEON_CYAN);
        
        qrContainer.add(qrTitle, BorderLayout.NORTH);
        qrContainer.add(qrCodePanel, BorderLayout.CENTER);
        
        ticketContent.add(qrContainer, BorderLayout.EAST);
        rightPanel.add(ticketContent, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
        
        // South Buttons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 16);
                g2.dispose();
            }
        };
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        southPanel.add(btnBack);
        southPanel.add(btnCancel);
        southPanel.add(btnConfirm);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    public void updateDisplay() {
        Object trajet = parent.getSelectedTrajet();
        String route = trajet != null ? trajet.toString() : "Douala ➔ Yaoundé";
        
        Object siege = parent.getSelectedSiege();
        String seat = siege != null ? siege.toString() : "Siège 00 (Classique)";
        
        double prix = parent.getPrixTotal();
        
        recapPanel.setData(route, seat, prix);
        prixLabel.setText("CREDITS REQUIS: " + String.format("%,.0f", prix * 1.15) + " CFA");
        
        nomField.setText("");
        cniField.setText("");
        qrCodePanel.generateNewPattern(); 
    }
    
    private void validateAndConfirm() {
        String nom = nomField.getText().trim();
        String cni = cniField.getText().trim();
        
        if (nom.isEmpty() || nom.length() < 2) {
            JOptionPane.showMessageDialog(this,
                "❌ Veuillez saisir un nom complet valide (min 2 caractères).",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            nomField.requestFocus();
            return;
        }
        
        if (cni.isEmpty() || cni.length() < 5 || !cni.matches("^[A-Za-z0-9]+$")) {
            JOptionPane.showMessageDialog(this,
                "❌ CNI requise (lettres/chiffres uniquement, min 5).",
                "Erreur de saisie",
                JOptionPane.WARNING_MESSAGE);
            cniField.requestFocus();
            return;
        }
        
        // Random bank rejection (5% chance)
        if (Math.random() < 0.05) {
            JOptionPane.showMessageDialog(this,
                "❌ TRANSACTION REFUSÉE PAR LE RÉSEAU BANCAIRE.\n" +
                "Raison: Réponse négative du serveur de crédit.\n" +
                "Veuillez vérifier votre compte et réessayer.",
                "Erreur de paiement",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 1. Instancier les objets métier du backend
        com.camrail.core.model.Trajet trajetModel = null;
        if (parent.getSelectedTrajet() instanceof com.camrail.core.model.Trajet) {
            trajetModel = (com.camrail.core.model.Trajet) parent.getSelectedTrajet();
        } else {
            // Reconstitution dynamique à partir de la sélection de l'IHM
            String depart = "Douala";
            String arrivee = "Yaoundé";
            String heure = "07:00";
            double prixBase = 5000;
            int dureeMin = 240;
            
            Object t = parent.getSelectedTrajet();
            if (t != null) {
                try {
                    java.lang.reflect.Method getDepart = t.getClass().getMethod("getDepart");
                    java.lang.reflect.Method getArrivee = t.getClass().getMethod("getArrivee");
                    java.lang.reflect.Method getHeure = t.getClass().getMethod("getHeure");
                    java.lang.reflect.Method getDuree = t.getClass().getMethod("getDuree");
                    java.lang.reflect.Method getPrix = t.getClass().getMethod("getPrix");
                    
                    depart = (String) getDepart.invoke(t);
                    arrivee = (String) getArrivee.invoke(t);
                    heure = (String) getHeure.invoke(t);
                    prixBase = (Double) getPrix.invoke(t);
                    
                    String durStr = (String) getDuree.invoke(t);
                    if (durStr != null && durStr.contains("h")) {
                        String[] parts = durStr.split("h");
                        int h = Integer.parseInt(parts[0].trim());
                        int m = parts.length > 1 && !parts[1].trim().isEmpty() ? Integer.parseInt(parts[1].trim()) : 0;
                        dureeMin = h * 60 + m;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            trajetModel = new com.camrail.core.model.Trajet(depart, arrivee, heure, prixBase, dureeMin);
        }
        
        com.camrail.core.model.ClasseVoyage classeModel = com.camrail.core.model.ClasseVoyage.ECONOMIQUE;
        String clStr = parent.getSelectionPanel().getSelectedClasse();
        if (clStr != null) {
            if (clStr.contains("Prem") || clStr.contains("1")) {
                classeModel = com.camrail.core.model.ClasseVoyage.PREMIERE;
            } else if (clStr.contains("Bus")) {
                classeModel = com.camrail.core.model.ClasseVoyage.BUSINESS;
            }
        }
        
        int seatNum = 22;
        if (parent.getSelectedSiege() != null) {
            try {
                if (parent.getSelectedSiege() instanceof Integer) {
                    seatNum = (Integer) parent.getSelectedSiege();
                } else {
                    String sStr = parent.getSelectedSiege().toString().replaceAll("[^0-9]", "");
                    if (!sStr.isEmpty()) {
                        seatNum = Integer.parseInt(sStr);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        com.camrail.core.model.Siege siegeModel = new com.camrail.core.model.Siege(seatNum, classeModel);
        
        com.camrail.core.model.Reservation reservation = new com.camrail.core.model.Reservation(trajetModel, siegeModel, nom);
        reservation.setTelephone(cni); // Stocke la CNI dans le champ téléphone ou passager
        reservation.validerPaiement();
        
        // 2. Persistance : sauvegarde sérialisée du ticket et BD SQLite
        try {
            com.camrail.persistance.dao.DataAccess.getInstance().sauvegarder(reservation);
            com.camrail.persistance.dao.DatabaseAccess.getInstance().sauvegarder(reservation);
        } catch (com.camrail.persistance.exception.PersistanceException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "⚠️ Données de sauvegarde hors ligne utilisées.",
                "Notification Persistance",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        // 3. Écriture physique du ticket sur disque (.txt dans tickets/)
        try {
            com.camrail.persistance.TicketWriter.genererTicket(reservation);
        } catch (com.camrail.persistance.exception.TicketException ex) {
            ex.printStackTrace();
        }
        
        // 4. Mettre à jour l'état de l'application
        parent.setNomPassager(nom);
        parent.setCniPassager(cni);
        parent.setTicketNumber(reservation.getNumeroTicket());
        
        // Code sécurité généré de style AZE-789-XYZ
        String codeSecurite = com.camrail.core.util.GenerateurCode.genererCodeSecurite();
        parent.setCodeSecurite(codeSecurite);
        
        parent.showPanel("IMPRESSION");
        parent.getImpressionPanel().startImpression();
    }
    
    private void confirmExit() {
        int response = JOptionPane.showConfirmDialog(this,
            "⚠️ Voulez-vous vraiment abandonner la réservation ?",
            "Annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            parent.resetAll();
        }
    }

    // High fidelity glassmorphic ticket details display
    private static class HolographicRecap extends JPanel {
        private String route = "Douala ➔ Yaoundé";
        private String seat = "Siège 23 (CLASSIQUE)";
        private double basePrice = 7500;
        private double surcharge = 1125;
        private double total = 8625;
        
        public HolographicRecap() {
            setOpaque(false);
            setPreferredSize(new Dimension(280, 240));
        }
        
        public void setData(String route, String seat, double price) {
            this.route = route != null ? route : "Non spécifié";
            this.seat = seat != null ? seat : "Non spécifié";
            this.basePrice = price;
            this.surcharge = price * 0.15;
            this.total = price * 1.15;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Theme.applyQualityRendering(g2);
            
            int w = getWidth();
            int h = getHeight();
            
            // Draw glass inner panel
            Theme.drawGlassPanel(g2, 0, 0, w, h, 16);
            Theme.drawNeonBorder(g2, 0, 0, w, h, 16, Theme.NEON_PURPLE, Theme.NEON_CYAN);
            
            // Header
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            g2.setColor(Theme.NEON_CYAN);
            g2.drawString("RECAPITULATIF DE SECURITE //", 18, 30);
            
            // Route section
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString("📍 TRAJET SELECTIONNE", 18, 55);
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(Color.WHITE);
            g2.drawString(route, 18, 73);
            
            // Seat section
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString("💺 CABINE & SELECTION", 18, 103);
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(Theme.NEON_CYAN);
            g2.drawString(seat, 18, 121);
            
            // Billing section
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString("💳 ALLOCATION DE CREDITS", 18, 152);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(Theme.TEXT_LIGHT);
            g2.drawString(String.format("Tarif Base :  %,.0f CFA", basePrice), 18, 172);
            g2.drawString(String.format("Surcharge (15%%) : %,.0f CFA", surcharge), 18, 190);
            
            // Thin cyan separator
            g2.setStroke(Theme.STROKE_1);
            g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 40));
            g2.drawLine(18, 202, w - 18, 202);
            
            // Total price
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(Theme.NEON_PINK);
            g2.drawString(String.format("TOTAL TRANSIT : %,.0f CFA", total), 18, 222);
            
            g2.dispose();
        }
    }

    private static class QRCodePanel extends JPanel {
        private boolean[][] qrMatrix = new boolean[21][21];
        private Random rand = new Random();
 
        public QRCodePanel() {
            setPreferredSize(new Dimension(100, 100));
            setMinimumSize(new Dimension(100, 100));
            setMaximumSize(new Dimension(100, 100));
            setOpaque(false);
            generateNewPattern();
        }
 
        public void generateNewPattern() {
            for (int r = 0; r < 21; r++) {
                for (int c = 0; c < 21; c++) {
                    qrMatrix[r][c] = rand.nextBoolean();
                }
            }
            drawSquarePattern(0, 0);
            drawSquarePattern(14, 0);
            drawSquarePattern(0, 14);
            repaint();
        }
 
        private void drawSquarePattern(int r, int c) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    boolean isBorder = (i == 0 || i == 6 || j == 0 || j == 6);
                    boolean isCenter = (i >= 2 && i <= 4 && j >= 2 && j <= 4);
                    qrMatrix[r + i][c + j] = isBorder || isCenter;
                }
            }
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Theme.applyQualityRendering(g2);
            
            int size = Math.min(getWidth(), getHeight()) - 10;
            int pxSize = size / 21;
            int startX = (getWidth() - size) / 2;
            int startY = (getHeight() - size) / 2;
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(startX - 4, startY - 4, size + 8, size + 8, 8, 8);
            
            g2.setColor(Color.BLACK);
            for (int r = 0; r < 21; r++) {
                for (int c = 0; c < 21; c++) {
                    if (qrMatrix[r][c]) {
                        g2.fillRect(startX + c * pxSize, startY + r * pxSize, pxSize, pxSize);
                    }
                }
            }
            g2.dispose();
        }
    }
}