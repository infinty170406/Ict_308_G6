package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class FuturisticBackground extends JPanel {
    private Image bgImage = null;

    public FuturisticBackground() {
        // Load the retro-futuristic synthwave trains background image
        try {
            bgImage = new ImageIcon("bg_train.jpg").getImage();
        } catch (Exception e) {
            System.err.println("Erreur chargement bg_train.jpg: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);
        
        int w = getWidth();
        int h = getHeight();
        
        // 1. Draw Synthwave Train Image or Fallback
        if (bgImage != null && bgImage.getWidth(null) > 0) {
            g2.drawImage(bgImage, 0, 0, w, h, null);
            // Cyber dark overlay to keep HUD panels readable and give a neon tint
            g2.setColor(new Color(8, 8, 22, 190)); 
            g2.fillRect(0, 0, w, h);
        } else {
            g2.setColor(Theme.BG_DARK);
            g2.fillRect(0, 0, w, h);
        }
        
        // 2. Ambient Glowing Nebulas (Overlay highlight)
        int radius1 = (int) (w * 0.35);
        int cx1 = (int) (w * 0.2);
        int cy1 = (int) (h * 0.8);
        if (radius1 > 0) {
            Point2D center = new Point2D.Float(cx1, cy1);
            float[] dist = {0.0f, 1.0f};
            Color[] colors = {Theme.getGlowColor(Theme.NEON_CYAN, 20), Theme.getGlowColor(Color.BLACK, 0)};
            RadialGradientPaint rgp = new RadialGradientPaint(center, radius1, dist, colors);
            g2.setPaint(rgp);
            g2.fillRect(0, 0, w, h);
        }

        // 3. Subtle Cyber Grid Lines drawn on top of the image
        g2.setStroke(Theme.STROKE_1);
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 8));
        
        int gridSpacing = 45;
        for (int x = 0; x < w; x += gridSpacing) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += gridSpacing) {
            g2.drawLine(0, y, w, y);
        }
        
        // 4. Horizon perspective line at the bottom
        g2.setColor(Theme.getGlowColor(Theme.NEON_PURPLE, 12));
        int horizon = (int) (h * 0.7);
        g2.drawLine(0, horizon, w, horizon);
        
        g2.dispose();
    }
}
