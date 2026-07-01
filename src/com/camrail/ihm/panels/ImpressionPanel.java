package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.components.CustomButton;
import com.camrail.ihm.components.ProgressPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImpressionPanel extends JPanel {
    private MainFrame parent;
    private ProgressPanel progressPanel;
    private JTextArea ticketPreview;
    private JButton btnCancel;
    private Timer animationTimer;
    private boolean isPrinting = false;
    private int progress = 0;
    private int errorCount = 0;
    
    public ImpressionPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        progressPanel = new ProgressPanel();
        progressPanel.setProgress(0);
        progressPanel.setStatus("Prêt à imprimer...");
        
        // ===== TEXTE D'APERÇU AVEC TAILLE SUFFISANTE =====
        ticketPreview = new JTextArea();
        ticketPreview.setEditable(false);
        ticketPreview.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ticketPreview.setBackground(new Color(250, 250, 250));
        ticketPreview.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        ticketPreview.setPreferredSize(new Dimension(450, 350));
        ticketPreview.setMinimumSize(new Dimension(400, 300));
        ticketPreview.setText("Aucun billet à afficher");
        
        btnCancel = new CustomButton("✕ Annuler", new Color(200, 50, 50));
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(e -> cancelPrint());
        
        animationTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProgress();
            }
        });
    }
    
    private void setupLayout() {
        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.setBackground(Color.WHITE);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));
        
        JLabel title = new JLabel("Impression du billet", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0, 51, 102));
        JLabel subtitle = new JLabel("Étape 3/3 - Veuillez patienter pendant l'impression", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        
        northPanel.add(title);
        northPanel.add(subtitle);
        add(northPanel, BorderLayout.NORTH);
        
        // ===== PANEL CENTRE AVEC GRIDBAGLAYOUT =====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Panneau de progression (gauche)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        centerPanel.add(progressPanel, gbc);
        
        // ===== APERÇU DU BILLET (droite) =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 51, 102), 2),
            "📄 Aperçu du billet",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(0, 51, 102)
        ));
        
        JScrollPane scrollPane = new JScrollPane(ticketPreview);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        centerPanel.add(rightPanel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));
        
        southPanel.add(btnCancel);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    public void startImpression() {
        // Générer l'aperçu AVANT de démarrer
        genererApercuTicket();
        
        isPrinting = true;
        progress = 0;
        errorCount = 0;
        btnCancel.setEnabled(true);
        progressPanel.setProgress(0);
        progressPanel.setStatus("Début de l'impression...");
        progressPanel.setIcon("🖨️");
        
        animationTimer.start();
    }
    
    private void updateProgress() {
        progress += 5;
        
        if (progress <= 100) {
            progressPanel.setProgress(progress);
            
            if (progress < 30) {
                progressPanel.setStatus("Préparation du billet...");
            } else if (progress < 60) {
                progressPanel.setStatus("Impression en cours...");
            } else if (progress < 90) {
                progressPanel.setStatus("Finalisation de l'impression...");
            } else {
                progressPanel.setStatus("Impression presque terminée...");
            }
            
            // Exception 12 : Fichier déjà existant (5% de chance)
            if (progress == 40 && Math.random() < 0.05) {
                animationTimer.stop();
                progressPanel.setError("Erreur : Fichier ticket déjà existant !");
                progressPanel.setIcon("❌");
                btnCancel.setEnabled(false);
                isPrinting = false;
                
                JOptionPane.showMessageDialog(this,
                    "❌ Erreur de sauvegarde : Un billet avec le numéro " + 
                    parent.getTicketNumber() + " existe déjà.\n\n" +
                    "Veuillez réessayer. Un nouveau numéro sera généré.",
                    "Fichier existant",
                    JOptionPane.ERROR_MESSAGE);
                
                String newTicket = "CAM-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
                                  "-" + String.format("%06d", new java.util.Random().nextInt(999999));
                parent.setTicketNumber(newTicket);
                
                Timer retryTimer = new Timer(2000, e -> {
                    progressPanel.reset();
                    startImpression();
                });
                retryTimer.setRepeats(false);
                retryTimer.start();
                return;
            }
            
        } else {
            // Impression réussie
            animationTimer.stop();
            progressPanel.setSuccess();
            progressPanel.setIcon("✅");
            btnCancel.setEnabled(false);
            isPrinting = false;
            
            JOptionPane.showMessageDialog(this,
                "✅ Billet imprimé avec succès !\n\n" +
                "📋 Ticket N°: " + parent.getTicketNumber() + "\n" +
                "🔐 Code sécurité: " + parent.getCodeSecurite() + "\n\n" +
                "📌 Conservez précieusement ce billet.\n" +
                "Il vous sera demandé à l'embarquement.",
                "Impression réussie",
                JOptionPane.INFORMATION_MESSAGE);
            
            Timer resetTimer = new Timer(3000, e -> parent.resetAll());
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }
    
    // ============================================
    // GÉNÉRATION DE L'APERÇU COMPLET
    // ============================================
    
    private void genererApercuTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════╗\n");
        sb.append("║              CAMRAIL INTERCITY                      ║\n");
        sb.append("║          Système de Réservation de Billets          ║\n");
        sb.append("╚══════════════════════════════════════════════════════╝\n\n");
        
        // ===== NUMÉRO DE TICKET =====
        String ticketNum = parent.getTicketNumber();
        if (ticketNum == null || ticketNum.isEmpty()) {
            ticketNum = "CAM-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
                       "-" + String.format("%06d", new java.util.Random().nextInt(999999));
            parent.setTicketNumber(ticketNum);
        }
        sb.append("  📋 TICKET N°: ").append(ticketNum).append("\n");
        
        // ===== DATE ET HEURE =====
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
        sb.append("  📅 DATE: ").append(sdf.format(new Date())).append("\n\n");
        
        sb.append("  ─────────────────────────────────────────────────────\n");
        sb.append("                   DÉTAILS DU VOYAGE                  \n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");
        
        // ===== TRAJET (avec heure de départ) =====
        Object trajet = parent.getSelectedTrajet();
        sb.append("  📍 TRAJET:\n");
        if (trajet != null) {
            // Récupérer les infos du trajet
            String trajetStr = trajet.toString();
            sb.append("     ").append(trajetStr).append("\n");
            
            // Extraire l'heure de départ si disponible
            if (trajetStr.contains("(") && trajetStr.contains(")")) {
                int start = trajetStr.indexOf("(");
                int end = trajetStr.indexOf(")");
                if (start != -1 && end != -1 && start < end) {
                    String heure = trajetStr.substring(start + 1, end);
                    sb.append("     ⏰ Départ: ").append(heure).append("\n");
                }
            }
        } else {
            sb.append("     Non spécifié\n");
        }
        sb.append("\n");
        
        // ===== SIÈGE =====
        Object siege = parent.getSelectedSiege();
        sb.append("  💺 SIÈGE:\n");
        if (siege != null) {
            sb.append("     ").append(siege.toString()).append("\n");
        } else {
            sb.append("     Non spécifié\n");
        }
        sb.append("\n");
        
        // ===== PASSAGER =====
        String nom = parent.getNomPassager();
        String cni = parent.getCniPassager();
        sb.append("  👤 PASSAGER:\n");
        if (nom != null && !nom.isEmpty()) {
            sb.append("     Nom: ").append(nom.toUpperCase()).append("\n");
        } else {
            sb.append("     Nom: Non spécifié\n");
        }
        if (cni != null && !cni.isEmpty()) {
            sb.append("     CNI: ").append(cni).append("\n");
        } else {
            sb.append("     CNI: Non spécifié\n");
        }
        sb.append("\n");
        
        // ===== PRIX =====
        double prix = parent.getPrixTotal();
        sb.append("  💰 PRIX:\n");
        if (prix > 0) {
            double taxes = prix * 0.15;
            double total = prix * 1.15;
            sb.append("     Prix de base: ").append(String.format("%,9.0f", prix)).append(" CFA\n");
            sb.append("     Taxes (15%):  ").append(String.format("%,9.0f", taxes)).append(" CFA\n");
            sb.append("     ─────────────────────────────────────\n");
            sb.append("     TOTAL:        ").append(String.format("%,9.0f", total)).append(" CFA\n");
        } else {
            sb.append("     Non défini\n");
        }
        sb.append("\n");
        
        // ===== CODE DE SÉCURITÉ =====
        String code = parent.getCodeSecurite();
        if (code == null || code.isEmpty()) {
            code = genererCodeSecurite();
            parent.setCodeSecurite(code);
        }
        sb.append("  🔐 CODE SÉCURITÉ: ").append(code).append("\n\n");
        
        // ===== CONDITIONS =====
        sb.append("  ─────────────────────────────────────────────────────\n");
        sb.append("  📌 CONDITIONS DE VOYAGE:\n");
        sb.append("     • Présenter ce billet à l'embarquement\n");
        sb.append("     • Une pièce d'identité sera exigée\n");
        sb.append("     • Arriver 30 minutes avant le départ\n");
        sb.append("     • Embarquement 15 minutes avant le départ\n");
        sb.append("  ─────────────────────────────────────────────────────\n\n");
        
        sb.append("  ╔══════════════════════════════════════════════════════╗\n");
        sb.append("  ║      Merci de voyager avec Camrail !                ║\n");
        sb.append("  ║      Bon voyage ! 🚆                                ║\n");
        sb.append("  ╚══════════════════════════════════════════════════════╝\n");
        
        ticketPreview.setText(sb.toString());
        
        // Forcer le rafraîchissement
        ticketPreview.revalidate();
        ticketPreview.repaint();
    }
    
    private String genererCodeSecurite() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    private void cancelPrint() {
        if (isPrinting) {
            int response = JOptionPane.showConfirmDialog(this,
                "⚠️ Êtes-vous sûr de vouloir annuler l'impression ?\n" +
                "Le billet ne sera pas émis.",
                "Confirmation d'annulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (response == JOptionPane.YES_OPTION) {
                animationTimer.stop();
                isPrinting = false;
                progressPanel.setError("Impression annulée");
                progressPanel.setIcon("⛔");
                btnCancel.setEnabled(false);
                
                Timer timer = new Timer(1500, e -> parent.showPanel("CONFIRMATION"));
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}