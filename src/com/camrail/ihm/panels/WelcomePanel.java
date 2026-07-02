package com.camrail.ihm.panels;

import com.camrail.ihm.MainFrame;
import com.camrail.ihm.Theme;
import com.camrail.ihm.components.CustomButton;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class WelcomePanel extends JPanel {
    private MainFrame parent;

    public WelcomePanel(MainFrame parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new BorderLayout());
        
        initComponents();
    }

    private void initComponents() {
        // Central glass panel
        JPanel centerContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), 24);
                
                // Draw Neon Header inside the glass panel
                Theme.drawNeonBorder(g2, 0, 0, getWidth(), getHeight(), 24, Theme.NEON_CYAN, Theme.NEON_PURPLE);
                g2.dispose();
            }
        };
        centerContainer.setOpaque(false);
        centerContainer.setLayout(new GridBagLayout());
        centerContainer.setPreferredSize(new Dimension(500, 360));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Brand logo/icon
        JLabel iconLbl = new JLabel("⚡", JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        iconLbl.setForeground(Theme.NEON_CYAN);
        gbc.gridy = 0;
        centerContainer.add(iconLbl, gbc);
        
        // Main Title
        JLabel titleLbl = new JLabel("CAMRAIL INTERCITY", JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(Theme.TEXT_LIGHT);
        gbc.gridy = 1;
        centerContainer.add(titleLbl, gbc);
        
        // Subtitle
        JLabel subLbl = new JLabel("TRANSIT PROTOCOL v2035", JLabel.CENTER);
        subLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        subLbl.setForeground(Theme.NEON_CYAN);
        gbc.gridy = 2;
        centerContainer.add(subLbl, gbc);
        
        // Description
        JLabel descLbl = new JLabel("<html><center>Découvrez l'avenir du voyage ferroviaire.<br>Vitesse maglev, confort holographique et réservation instantanée.</center></html>", JLabel.CENTER);
        descLbl.setFont(Theme.FONT_SUBTITLE);
        descLbl.setForeground(Theme.TEXT_MUTED);
        gbc.gridy = 3;
        centerContainer.add(descLbl, gbc);
        
        // Launch Button (custom neon style)
        JButton enterBtn = new CustomButton("INITIALISER LE TRANSIT", Theme.NEON_BLUE) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 80));
                g2.setStroke(Theme.STROKE_2);
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                g2.dispose();
            }
        };
        enterBtn.addActionListener(e -> {
            parent.showPanel("SELECTION");
        });
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 50, 15, 50);
        centerContainer.add(enterBtn, gbc);
        
        // Wrap center container in outer layout to center it
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(centerContainer);
        
        add(wrapper, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);
        
        int w = getWidth();
        int h = getHeight();
        
        // 1. Draw glowing maglev tracks
        int trackY = (int) (h * 0.75);
        g2.setStroke(Theme.STROKE_3);
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 60));
        g2.drawLine(0, trackY, w, trackY);
        g2.setStroke(Theme.STROKE_1);
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 120));
        g2.drawLine(0, trackY - 4, w, trackY - 4);
        g2.drawLine(0, trackY + 4, w, trackY + 4);
        
        // 2. Center the futuristic sleek train statically
        int trainWidth = 320;
        int trainHeight = 30;
        double trainX = w / 2 - trainWidth / 2;
        
        g2.translate(trainX, trackY - 25);
        
        // Train Body gradient
        GradientPaint bodyGrad = new GradientPaint(0, 0, new Color(30, 35, 55), trainWidth, 0, new Color(10, 15, 25));
        g2.setPaint(bodyGrad);
        
        // Sleek aerodynamic head shape
        Path2D.Double trainShape = new Path2D.Double();
        trainShape.moveTo(0, 10);
        trainShape.curveTo(80, 5, 200, 2, 260, 0); // top curve
        trainShape.curveTo(290, 0, 310, 10, 320, 20); // nose curve
        trainShape.lineTo(300, 30); // bottom nose
        trainShape.lineTo(0, 30);
        trainShape.closePath();
        g2.fill(trainShape);
        
        // Train Cabin Window (neon cyan outline)
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 180));
        g2.setStroke(Theme.STROKE_1_5);
        Path2D.Double windowShape = new Path2D.Double();
        windowShape.moveTo(220, 5);
        windowShape.lineTo(255, 4);
        windowShape.lineTo(275, 14);
        windowShape.lineTo(240, 15);
        windowShape.closePath();
        g2.fill(windowShape);
        
        // Neon stripe running along body
        g2.setStroke(Theme.STROKE_2);
        g2.setColor(Theme.NEON_CYAN);
        g2.drawLine(20, 22, 230, 22);
        
        // Headlight glow
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 200));
        g2.fill(new Ellipse2D.Double(295, 16, 8, 8));
        
        // Light ray from headlight (static radial gradient)
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {Theme.getGlowColor(Theme.NEON_CYAN, 80), Theme.getGlowColor(Theme.NEON_CYAN, 0)};
        RadialGradientPaint lightRay = new RadialGradientPaint(
            new Point2D.Double(300, 20), 80, dist, colors
        );
        g2.setPaint(lightRay);
        g2.fill(new Arc2D.Double(260, -20, 140, 80, -30, 60, Arc2D.PIE));
        
        g2.dispose();
    }
}
