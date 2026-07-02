package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.Theme;
import com.camrail.ihm.components.CustomButton;
import com.camrail.ihm.components.HolographicTicket;
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
    private HolographicTicket ticketPreview;
    private JButton btnCancel;
    private Timer animationTimer;
    private boolean isPrinting = false;
    private int progress = 0;
    
    public ImpressionPanel(MainFrame parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        progressPanel = new ProgressPanel();
        progressPanel.setProgress(0);
        progressPanel.setStatus("PRÊT À L'ÉMISSION...");
        
        ticketPreview = new HolographicTicket();
        
        btnCancel = new CustomButton("✕ ANNULER L'ÉMISSION", Theme.NEON_PINK);
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
        
        JLabel title = new JLabel("ÉMISSION DU BILLET SECURISE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT_LIGHT);
        
        JLabel subtitle = new JLabel("STAGE 03 // GÉNÉRATION DE LA CLEF D'ACCÈS DU TRANSIT");
        subtitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        subtitle.setForeground(Theme.NEON_CYAN);
        
        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(subtitle, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        
        // Center GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Circular Progress (left)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        centerPanel.add(progressPanel, gbc);
        
        // Ticket preview (right)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(ticketPreview, BorderLayout.CENTER);
        
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        centerPanel.add(rightPanel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // South Controls
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 16);
                g2.dispose();
            }
        };
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        southPanel.add(btnCancel);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    public void startImpression() {
        genererApercuTicket();
        
        isPrinting = true;
        progress = 0;
        btnCancel.setEnabled(true);
        progressPanel.setProgress(0);
        progressPanel.setStatus("INITIATION D'EMISSION...");
        progressPanel.setIcon("🖨️");
        
        animationTimer.start();
    }
    
    private void updateProgress() {
        progress += 5;
        
        if (progress <= 100) {
            progressPanel.setProgress(progress);
            
            if (progress < 30) {
                progressPanel.setStatus("CRYPTAGE DU PASS DE TRANSIT...");
            } else if (progress < 60) {
                progressPanel.setStatus("LIAISON RÉSEAU CRYPTOSEC...");
            } else if (progress < 90) {
                progressPanel.setStatus("SYNCHRONISATION QUANTIQUE...");
            } else {
                progressPanel.setStatus("VÉRIFICATION INTEGRITÉ...");
            }
            
            // Simulated error warning bypass (5% chance)
            if (progress == 40 && Math.random() < 0.05) {
                animationTimer.stop();
                progressPanel.setError("TICKET_ID EXISTANT");
                progressPanel.setIcon("❌");
                btnCancel.setEnabled(false);
                isPrinting = false;
                
                JOptionPane.showMessageDialog(this,
                    "❌ IDENTIFIANT EN CONFLIT : Un ticket avec le numéro " + 
                    parent.getTicketNumber() + " existe déjà.\n" +
                    "Génération d'une nouvelle signature d'accès...",
                    "Conflit de signature",
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
            animationTimer.stop();
            progressPanel.setSuccess();
            progressPanel.setIcon("✅");
            btnCancel.setEnabled(false);
            isPrinting = false;
            
            JOptionPane.showMessageDialog(this,
                "✅ ENREGISTREMENT ET ÉMISSION RÉUSSIS !\n\n" +
                "📋 ID TRANSIT: " + parent.getTicketNumber() + "\n" +
                "🔐 SÉCURITÉ: " + parent.getCodeSecurite() + "\n\n" +
                "Veuillez présenter la clé numérique hologramme lors de l'embarquement.",
                "Accès Autorisé",
                JOptionPane.INFORMATION_MESSAGE);
            
            Timer resetTimer = new Timer(3000, e -> parent.resetAll());
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }
    
    private void genererApercuTicket() {
        String ticketNum = parent.getTicketNumber();
        if (ticketNum == null || ticketNum.isEmpty()) {
            ticketNum = "CAM-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
                       "-" + String.format("%06d", new java.util.Random().nextInt(999999));
            parent.setTicketNumber(ticketNum);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
        String date = sdf.format(new Date());
        
        Object trajet = parent.getSelectedTrajet();
        String route = trajet != null ? trajet.toString() : "Douala ➔ Yaoundé";
        
        Object siege = parent.getSelectedSiege();
        String seat = siege != null ? siege.toString() : "Siège 00 (Classique)";
        
        String nom = parent.getNomPassager();
        String cni = parent.getCniPassager();
        double prix = parent.getPrixTotal();
        String code = parent.getCodeSecurite();
        if (code == null || code.isEmpty()) {
            code = genererCodeSecurite();
            parent.setCodeSecurite(code);
        }
        
        ticketPreview.setTicketData(ticketNum, date, route, seat, nom, cni, prix * 1.15, code);
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
                "⚠️ Voulez-vous interrompre la phase d'émission ?",
                "Interruption",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (response == JOptionPane.YES_OPTION) {
                animationTimer.stop();
                isPrinting = false;
                progressPanel.setError("ÉMISSION INTERROMPUE");
                progressPanel.setIcon("⛔");
                btnCancel.setEnabled(false);
                
                Timer timer = new Timer(1500, e -> parent.showPanel("CONFIRMATION"));
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}