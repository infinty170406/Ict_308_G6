package com.camrail.ihm;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;

public class Theme {
    public static final Color BG_DARK = new Color(8, 8, 16);
    public static final Color BG_CARD = new Color(18, 18, 35, 140);
    public static final Color BG_GLASS = new Color(15, 15, 30, 160);
    
    public static final Color NEON_CYAN = new Color(0, 240, 255);
    public static final Color NEON_BLUE = new Color(0, 110, 255);
    public static final Color NEON_PURPLE = new Color(189, 0, 255);
    public static final Color NEON_PINK = new Color(255, 0, 127);
    public static final Color TEXT_LIGHT = new Color(240, 244, 255);
    public static final Color TEXT_MUTED = new Color(140, 145, 175);
    
    // Cached colors to avoid continuous GC allocation
    public static final Color GLASS_SHADOW = new Color(0, 0, 0, 120);
    public static final Color GLASS_BORDER_TOP = new Color(255, 255, 255, 45);
    public static final Color GLASS_BORDER_BOTTOM = new Color(255, 255, 255, 5);
    
    // Cached strokes
    public static final BasicStroke STROKE_1 = new BasicStroke(1.0f);
    public static final BasicStroke STROKE_1_2 = new BasicStroke(1.2f);
    public static final BasicStroke STROKE_1_5 = new BasicStroke(1.5f);
    public static final BasicStroke STROKE_2 = new BasicStroke(2.0f);
    public static final BasicStroke STROKE_3 = new BasicStroke(3.0f);
    public static final BasicStroke STROKE_4 = new BasicStroke(4.0f);
    public static final BasicStroke STROKE_8 = new BasicStroke(8.0f);
    
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    
    // Cache for glow colors to prevent recreation
    private static final Map<Integer, Color> glowColorCache = new HashMap<>();
    
    public static Color getGlowColor(Color base, int alpha) {
        int key = base.getRGB() ^ (alpha << 24);
        Color cached = glowColorCache.get(key);
        if (cached == null) {
            cached = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
            glowColorCache.put(key, cached);
        }
        return cached;
    }
    
    public static void applyQualityRendering(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
    
    public static void drawGlassPanel(Graphics2D g2, int x, int y, int w, int h, int round) {
        // Shadow/glow background
        g2.setColor(GLASS_SHADOW);
        g2.fillRoundRect(x + 2, y + 4, w - 4, h - 4, round, round);
        
        // Glass fill
        g2.setColor(BG_GLASS);
        g2.fillRoundRect(x, y, w, h, round, round);
        
        // Inner gradient highlight border
        GradientPaint gp = new GradientPaint(x, y, GLASS_BORDER_TOP, x, y + h, GLASS_BORDER_BOTTOM);
        g2.setPaint(gp);
        g2.setStroke(STROKE_1_2);
        g2.drawRoundRect(x, y, w, h, round, round);
    }

    public static void drawNeonBorder(Graphics2D g2, int x, int y, int w, int h, int round, Color c1, Color c2) {
        GradientPaint gp = new GradientPaint(x, y, c1, x + w, y + h, c2);
        g2.setPaint(gp);
        g2.setStroke(STROKE_2);
        g2.drawRoundRect(x, y, w, h, round, round);
        
        // Subtle outer glow
        g2.setStroke(STROKE_4);
        g2.setColor(getGlowColor(c1, 30));
        g2.drawRoundRect(x - 1, y - 1, w + 2, h + 2, round, round);
    }
}
