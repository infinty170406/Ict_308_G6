package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ProgressPanel extends JPanel {
    private int progress = 0;
    private String statusText = "En attente...";
    private String iconText = "🖨️";
    private double angleOffset = 0.0;
    private double scanPos = 0.0; // Horizontal scanning laser position
    private Timer spinnerTimer;
    private Color activeColor = Theme.NEON_CYAN;

    public ProgressPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 350));
        setMinimumSize(new Dimension(250, 300));
        
        // Spin animation for tech radar rings (does not start automatically)
        spinnerTimer = new Timer(30, e -> {
            angleOffset += 0.03;
            scanPos += 0.015;
            if (scanPos > 1.0) {
                scanPos = 0.0;
            }
            repaint();
        });
    }

    public void setProgress(int value) {
        this.progress = value;
        if (value == 100) {
            this.activeColor = new Color(46, 204, 113); // Green success
            if (spinnerTimer.isRunning()) {
                spinnerTimer.stop();
            }
        } else if (value > 0) {
            this.activeColor = Theme.NEON_CYAN;
            if (!spinnerTimer.isRunning()) {
                spinnerTimer.start();
            }
        } else {
            this.activeColor = Theme.NEON_CYAN;
            if (spinnerTimer.isRunning()) {
                spinnerTimer.stop();
            }
        }
        repaint();
    }

    public void setStatus(String status) {
        this.statusText = status;
        repaint();
    }

    public void reset() {
        setProgress(0);
        setStatus("En attente...");
        this.activeColor = Theme.NEON_CYAN;
        this.iconText = "🖨️";
        repaint();
    }

    public void setIcon(String icon) {
        this.iconText = icon;
        repaint();
    }

    public void setSuccess() {
        setProgress(100);
        setStatus("BILLET ÉMIS AVEC SUCCÈS");
        this.iconText = "✅";
        this.activeColor = new Color(46, 204, 113);
        if (spinnerTimer.isRunning()) {
            spinnerTimer.stop();
        }
        repaint();
    }

    public void setError(String message) {
        setStatus("ERREUR: " + message);
        this.iconText = "❌";
        this.activeColor = Theme.NEON_PINK;
        if (spinnerTimer.isRunning()) {
            spinnerTimer.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw outer glass container
        Theme.drawGlassPanel(g2, 0, 0, w, h, 20);
        Theme.drawNeonBorder(g2, 0, 0, w, h, 20, activeColor, Theme.NEON_PURPLE);

        // 2. Holographic Radar Spinner
        int cx = w / 2;
        int cy = h / 2 - 20;
        int radius = 80;

        // Draw rotating dashed tech ring
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{12.0f, 6.0f}, 0.0f));
        g2.setColor(new Color(activeColor.getRed(), activeColor.getGreen(), activeColor.getBlue(), 60));
        
        Graphics2D gSpin = (Graphics2D) g2.create();
        gSpin.translate(cx, cy);
        gSpin.rotate(angleOffset);
        gSpin.draw(new Ellipse2D.Double(-radius - 8, -radius - 8, (radius + 8)*2, (radius + 8)*2));
        gSpin.rotate(-angleOffset * 2.0); // reverse rotation for inner ring
        gSpin.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f, 12.0f}, 0.0f));
        gSpin.draw(new Ellipse2D.Double(-radius + 8, -radius + 8, (radius - 8)*2, (radius - 8)*2));
        gSpin.dispose();

        // Draw main progress circle background
        g2.setStroke(Theme.STROKE_8);
        g2.setColor(new Color(255, 255, 255, 10));
        g2.draw(new Ellipse2D.Double(cx - radius, cy - radius, radius*2, radius*2));

        // Draw circular progress arc
        g2.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(activeColor);
        int angle = (int) (-360 * (progress / 100.0));
        g2.drawArc(cx - radius, cy - radius, radius*2, radius*2, 90, angle);

        // Draw percentage text inside circle
        g2.setFont(new Font("Monospaced", Font.BOLD, 26));
        g2.setColor(Color.WHITE);
        String pctStr = progress + "%";
        FontMetrics fm = g2.getFontMetrics();
        int px = cx - fm.stringWidth(pctStr) / 2;
        int py = cy + fm.getAscent() / 2 - 10;
        g2.drawString(pctStr, px, py);

        // Draw smaller icon/label below percentage
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        FontMetrics fmIcon = g2.getFontMetrics();
        g2.drawString(iconText, cx - fmIcon.stringWidth(iconText) / 2, cy + 28);

        // 3. Horizontal laser scanning sweep line (only during print progression)
        if (progress > 0 && progress < 100) {
            int scanY = 30 + (int)(scanPos * (h - 75));
            g2.setStroke(Theme.STROKE_2);
            g2.setColor(Theme.NEON_CYAN);
            g2.drawLine(15, scanY, w - 15, scanY);
            
            // Outer laser neon glow
            g2.setStroke(Theme.STROKE_4);
            g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 50));
            g2.drawLine(15, scanY, w - 15, scanY);
        }

        // 4. Status messages below spinner
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(activeColor);
        FontMetrics fmStatus = g2.getFontMetrics();
        String displayStatus = statusText.toUpperCase();
        g2.drawString(displayStatus, cx - fmStatus.stringWidth(displayStatus) / 2, h - 35);

        // Decorative technology interface grid stats
        g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString("CH.SECURE_LINK // EMISSION.A10", 20, 45);
        g2.drawString("PRT: INTERCITY_MAGLEV", w - 135, 45);

        g2.dispose();
    }
}