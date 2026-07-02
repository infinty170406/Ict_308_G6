package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;

public class RailNetworkMap extends JPanel {
    private Map<String, Point> stations;
    private String activeDepart = "";
    private String activeArrivee = "";

    public RailNetworkMap() {
        setOpaque(false);
        setPreferredSize(new Dimension(380, 200));
        setMinimumSize(new Dimension(300, 180));

        // Define station coordinates (relative to panel size)
        stations = new HashMap<>();
        stations.put("Douala", new Point(100, 140));
        stations.put("Yaoundé", new Point(180, 130));
        stations.put("Bafoussam", new Point(110, 60));
        stations.put("Ngaoundéré", new Point(280, 50));
        
        // No timer. Static rendering to avoid CPU consumption.
    }

    public void setActiveRoute(String depart, String arrivee) {
        this.activeDepart = depart;
        this.activeArrivee = arrivee;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Draw glass border & background
        Theme.drawGlassPanel(g2, 0, 0, w, h, 16);
        Theme.drawNeonBorder(g2, 0, 0, w, h, 16, Theme.getGlowColor(Theme.NEON_CYAN, 30), Theme.getGlowColor(Theme.NEON_PURPLE, 30));

        // Draw Map Title
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(Theme.NEON_CYAN);
        g2.drawString("SYS.CARTE.RESEAU // LIAISONS ACTIVES", 20, 25);

        // Draw Map Grids/Grid points (cyberpunk coordinate style)
        g2.setColor(Theme.getGlowColor(Color.WHITE, 10));
        for (int x = 20; x < w; x += 30) {
            for (int y = 30; y < h; y += 30) {
                g2.fillOval(x, y, 2, 2);
            }
        }

        // 1. Draw Network Connections (static lines)
        g2.setStroke(Theme.STROKE_1_5);
        g2.setColor(Theme.getGlowColor(Color.WHITE, 25));
        
        drawRoute(g2, "Douala", "Yaoundé");
        drawRoute(g2, "Douala", "Bafoussam");
        drawRoute(g2, "Douala", "Ngaoundéré");
        drawRoute(g2, "Yaoundé", "Bafoussam");

        // 2. Draw Active Highlight Route
        if (stations.containsKey(activeDepart) && stations.containsKey(activeArrivee)) {
            Point p1 = stations.get(activeDepart);
            Point p2 = stations.get(activeArrivee);

            // Scale to current size
            Point s1 = scalePoint(p1, w, h);
            Point s2 = scalePoint(p2, w, h);

            // Neon glowing line
            g2.setStroke(Theme.STROKE_3);
            g2.setColor(Theme.getGlowColor(Theme.NEON_PURPLE, 60));
            g2.drawLine(s1.x, s1.y, s2.x, s2.y);

            g2.setStroke(Theme.STROKE_1_5);
            g2.setColor(Theme.NEON_CYAN);
            g2.drawLine(s1.x, s1.y, s2.x, s2.y);

            // Pulse point static highlight in the center
            double px = s1.x + (s2.x - s1.x) * 0.5;
            double py = s1.y + (s2.y - s1.y) * 0.5;
            
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(px - 4, py - 4, 8, 8));
            g2.setColor(Theme.NEON_CYAN);
            g2.setStroke(Theme.STROKE_1_5);
            g2.draw(new Ellipse2D.Double(px - 7, py - 7, 14, 14));
        }

        // 3. Draw Nodes (Stations)
        for (Map.Entry<String, Point> entry : stations.entrySet()) {
            String name = entry.getKey();
            Point pt = scalePoint(entry.getValue(), w, h);
            
            boolean isActive = name.equals(activeDepart) || name.equals(activeArrivee);
            
            if (isActive) {
                // Large cyan pulsing node
                g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 40));
                g2.fill(new Ellipse2D.Double(pt.x - 12, pt.y - 12, 24, 24));
                g2.setColor(Theme.NEON_CYAN);
                g2.fill(new Ellipse2D.Double(pt.x - 6, pt.y - 6, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Double(pt.x - 3, pt.y - 3, 6, 6));
            } else {
                // Standard node
                g2.setColor(Theme.getGlowColor(Color.WHITE, 40));
                g2.fill(new Ellipse2D.Double(pt.x - 8, pt.y - 8, 16, 16));
                g2.setColor(Theme.TEXT_MUTED);
                g2.fill(new Ellipse2D.Double(pt.x - 4, pt.y - 4, 8, 8));
            }

            // Node Labels
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            g2.setColor(isActive ? Color.WHITE : Theme.TEXT_MUTED);
            g2.drawString(name.toUpperCase(), pt.x + 12, pt.y + 4);
        }

        g2.dispose();
    }

    private void drawRoute(Graphics2D g2, String s1Name, String s2Name) {
        Point p1 = stations.get(s1Name);
        Point p2 = stations.get(s2Name);
        if (p1 != null && p2 != null) {
            Point s1 = scalePoint(p1, getWidth(), getHeight());
            Point s2 = scalePoint(p2, getWidth(), getHeight());
            g2.drawLine(s1.x, s1.y, s2.x, s2.y);
        }
    }

    private Point scalePoint(Point p, int w, int h) {
        // Base mapping size was 380x200
        double rx = (double) w / 380;
        double ry = (double) h / 200;
        return new Point((int)(p.x * rx), (int)(p.y * ry));
    }
}
