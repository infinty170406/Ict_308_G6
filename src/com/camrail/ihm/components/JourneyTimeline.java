package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class JourneyTimeline extends JPanel {
    private String depart = "";
    private String arrivee = "";
    private String duration = "";
    private String[] midStations = {};
    private double trainPos = 0.45; // Static representation of travel path

    public JourneyTimeline() {
        setOpaque(false);
        setPreferredSize(new Dimension(500, 75));
        setMinimumSize(new Dimension(300, 65));
        
        // No timer. Completely static.
    }

    public void setRoute(String depart, String arrivee, String duration) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.duration = duration;
        
        // Calculate intermediate stops based on route
        if (depart.equals("Douala") && arrivee.equals("Yaoundé")) {
            midStations = new String[]{"Édéa", "Eseka"};
        } else if (depart.equals("Yaoundé") && arrivee.equals("Douala")) {
            midStations = new String[]{"Eseka", "Édéa"};
        } else if (depart.equals("Douala") && arrivee.equals("Ngaoundéré")) {
            midStations = new String[]{"Yaoundé", "Bélabo"};
        } else if (depart.equals("Ngaoundéré") && arrivee.equals("Douala")) {
            midStations = new String[]{"Bélabo", "Yaoundé"};
        } else {
            midStations = new String[]{"Transit Node"};
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

        // If no route set, display idle message
        if (depart.isEmpty() || arrivee.isEmpty()) {
            g2.setFont(Theme.FONT_SUBTITLE);
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString("TIMELINE: En attente de trajet...", w / 2 - 100, h / 2 + 5);
            g2.dispose();
            return;
        }

        // Draw timeline line
        int lineY = h / 2 - 5;
        int startX = 50;
        int endX = w - 50;
        int lineW = endX - startX;

        // Draw track base
        g2.setColor(Theme.getGlowColor(Color.WHITE, 20));
        g2.setStroke(Theme.STROKE_4);
        g2.drawLine(startX, lineY, endX, lineY);

        g2.setColor(Theme.NEON_CYAN);
        g2.setStroke(Theme.STROKE_1_5);
        g2.drawLine(startX, lineY, endX, lineY);

        // Station count
        int numNodes = 2 + midStations.length;
        
        // Draw Departure Node
        drawStationNode(g2, startX, lineY, depart, true);

        // Draw Intermediate Nodes
        for (int i = 0; i < midStations.length; i++) {
            double ratio = (double) (i + 1) / (numNodes - 1);
            int nx = startX + (int)(lineW * ratio);
            drawStationNode(g2, nx, lineY, midStations[i], false);
        }

        // Draw Arrival Node
        drawStationNode(g2, endX, lineY, arrivee, true);

        // Draw Static Train Icon
        int tx = startX + (int)(lineW * trainPos);
        int ty = lineY - 12;
        
        g2.setColor(Theme.NEON_PINK);
        g2.fillRoundRect(tx - 10, ty, 20, 10, 4, 4);
        g2.setColor(Color.WHITE);
        g2.fill(new Arc2D.Double(tx + 4, ty + 2, 6, 6, -90, 180, Arc2D.PIE));
        
        // Add tiny static tail trail
        g2.setColor(Theme.getGlowColor(Theme.NEON_PINK, 80));
        g2.fillRect(tx - 25, ty + 3, 12, 4);

        g2.dispose();
    }

    private void drawStationNode(Graphics2D g2, int x, int y, String name, boolean isMajor) {
        if (isMajor) {
            g2.setColor(Theme.NEON_PURPLE);
            g2.fill(new Ellipse2D.Double(x - 6, y - 6, 12, 12));
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(x - 3, y - 3, 6, 6));
        } else {
            g2.setColor(Theme.NEON_CYAN);
            g2.fill(new Ellipse2D.Double(x - 4, y - 4, 8, 8));
            g2.setColor(Theme.BG_DARK);
            g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
        }

        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(Theme.TEXT_LIGHT);
        FontMetrics fm = g2.getFontMetrics();
        int labelX = x - fm.stringWidth(name) / 2;
        g2.drawString(name, labelX, y + 20);
    }
}
