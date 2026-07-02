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
    private JTextArea recapArea;
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
        // Passenger Name field
        nomField = createCyberTextField();
        // CNI field
        cniField = createCyberTextField();
        
        // QR Code Preview panel
        qrCodePanel = new QRCodePanel();
        
        // Ticket Recap Area
        recapArea = new JTextArea();
        recapArea.setEditable(false);
        recapArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        recapArea.setBackground(new Color(12, 12, 24));
        recapArea.setForeground(Theme.NEON_CYAN);
        recapArea.setCaretColor(Theme.NEON_CYAN);
        recapArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Price Badge
        prixLabel = new JLabel("", JLabel.CENTER);
        prixLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        prixLabel.setForeground(Theme.NEON_PINK);
        
        // Buttons
        btnBack = new CustomButton("← RETOUR", new Color(100, 100, 115));
        btnConfirm = new CustomButton("💳 TRANSACT ET PAYER", Theme.NEON_BLUE);
        btnCancel = new CustomButton("✕ ANNULER", Theme.NEON_PINK);
        
        btnBack.addActionListener(e -> parent.showPanel("SELECTION"));
        btnConfirm.addActionListener(e -> validateAndConfirm());
        btnCancel.addActionListener(e -> confirmExit());
    }
    
    private JTextField createCyberTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.BOLD, 14));
        field.setBackground(new Color(15, 15, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Theme.NEON_CYAN);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 240, 255, 60), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Glow effect on focus
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.NEON_CYAN, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 240, 255, 60), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerLocation(420);
        splitPane.setBorder(null);
        
        // Left Panel - Passenger Biometrics inputs
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
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Large Biometric Fingerprint/Identity icon
        JLabel iconLabel = new JLabel("👤", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setForeground(Theme.NEON_CYAN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftPanel.add(iconLabel, gbc);
        
        // Input Labels
        JLabel nomLbl = new JLabel("NOM DU PASSAGER :");
        nomLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        nomLbl.setForeground(Theme.TEXT_LIGHT);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        leftPanel.add(nomLbl, gbc);
        
        gbc.gridx = 1;
        leftPanel.add(nomField, gbc);
        
        JLabel cniLbl = new JLabel("NUMÉRO CNI :");
        cniLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        cniLbl.setForeground(Theme.TEXT_LIGHT);
        gbc.gridy = 2;
        gbc.gridx = 0;
        leftPanel.add(cniLbl, gbc);
        
        gbc.gridx = 1;
        leftPanel.add(cniField, gbc);
        
        // Vertical spacer
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        leftPanel.add(Box.createVerticalStrut(25), gbc);
        
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
        priceContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        priceContainer.add(prixLabel, BorderLayout.CENTER);
        
        gbc.gridy = 4;
        leftPanel.add(priceContainer, gbc);
        
        // Right Panel - Holographic Ticket Boarding Pass
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 20);
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 20, Theme.NEON_PURPLE, Theme.NEON_CYAN);
                
                // Holographic design lines
                g2.setColor(new Color(0, 240, 255, 20));
                g2.setStroke(new BasicStroke(1.0f));
                for (int y = 30; y < getHeight(); y += 40) {
                    g2.drawLine(20, y, getWidth() - 20, y);
                }
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ScrollPane for text
        JScrollPane scrollPane = new JScrollPane(recapArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 0, 255, 60), 2));
        
        // Place QR Code and details together in right pane
        JPanel ticketContent = new JPanel(new BorderLayout(10, 0));
        ticketContent.setOpaque(false);
        ticketContent.add(scrollPane, BorderLayout.CENTER);
        
        // Wrap QR Panel in container to align it nicely
        JPanel qrContainer = new JPanel(new BorderLayout());
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
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("      PROTOCOLE DE VOYAGE DÉCRYPTÉ // RESUMÉ    \n");
        sb.append("================================================\n\n");
        
        Object trajet = parent.getSelectedTrajet();
        if (trajet != null) {
            sb.append("📍 TRANSIT DIRECT:\n");
            sb.append("   ").append(trajet.toString()).append("\n\n");
        }
        
        Object siege = parent.getSelectedSiege();
        if (siege != null) {
            sb.append("💺 POSITION CABINE:\n");
            sb.append("   ").append(siege.toString()).append("\n\n");
        }
        
        double prix = parent.getPrixTotal();
        sb.append("💸 CREDITS TRANSIT:\n");
        sb.append("   Tarif Base:  ").append(String.format("%,.0f", prix)).append(" CFA\n");
        sb.append("   Surcharge:   ").append(String.format("%,.0f", prix * 0.15)).append(" CFA\n");
        sb.append("   ─────────────────────────────────────\n");
        sb.append("   TOTAL FINAL: ").append(String.format("%,.0f", prix * 1.15)).append(" CFA\n\n");
        sb.append("================================================\n");
        sb.append("⚡ ENREGISTREZ VOTRE IDENTITÉ BIOMÉTRIQUE\n");
        sb.append("================================================\n");
        
        recapArea.setText(sb.toString());
        prixLabel.setText("CREDITS: " + String.format("%,.0f", prix * 1.15) + " CFA");
        
        nomField.setText("");
        cniField.setText("");
        qrCodePanel.generateNewPattern(); // Refresh fake QR Code pattern
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
        
        // Random payment refusal exception (5% chance)
        if (Math.random() < 0.05) {
            JOptionPane.showMessageDialog(this,
                "❌ TRANSACTION REFUSÉE PAR LE RÉSEAU BANCAIRE.\n" +
                "Raison: Réponse négative du serveur de crédit.\n" +
                "Veuillez vérifier votre compte et réessayer.",
                "Erreur de paiement",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
        int response = JOptionPane.showConfirmDialog(this,
            "⚠️ Voulez-vous vraiment abandonner la réservation ?",
            "Annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            parent.resetAll();
        }
    }

    // A panel that draws a real looking fake QR Code
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
            // Generate random QR pixels
            for (int r = 0; r < 21; r++) {
                for (int c = 0; c < 21; c++) {
                    qrMatrix[r][c] = rand.nextBoolean();
                }
            }
            
            // Draw standard 3 corner alignment patterns
            drawSquarePattern(0, 0);
            drawSquarePattern(14, 0);
            drawSquarePattern(0, 14);
            repaint();
        }

        private void drawSquarePattern(int r, int c) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    // Border of alignment mark
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
            
            // Draw white background card for QR Code
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(startX - 4, startY - 4, size + 8, size + 8, 8, 8);
            
            // Draw pixels
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