package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class CustomButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    public CustomButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setPreferredSize(new Dimension(180, 42));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    public void setBaseColor(Color color) {
        this.baseColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        repaint();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);
        
        int w = getWidth();
        int h = getHeight();
        
        // Color select based on state
        Color bgCol = baseColor;
        Color borderCol = baseColor.brighter();
        
        if (!isEnabled()) {
            bgCol = new Color(70, 70, 80, 100);
            borderCol = new Color(100, 100, 110, 80);
        } else if (isPressed) {
            bgCol = pressedColor;
            borderCol = baseColor;
        } else if (isHovered) {
            bgCol = hoverColor;
            borderCol = Theme.NEON_CYAN;
        }
        
        // 1. Draw Glassmorphism background for button
        g2.setColor(new Color(bgCol.getRed(), bgCol.getGreen(), bgCol.getBlue(), 120));
        g2.fillRoundRect(2, 2, w - 4, h - 4, 12, 12);
        
        // 2. Draw glowing neon border
        g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.2f));
        g2.setColor(borderCol);
        g2.drawRoundRect(2, 2, w - 4, h - 4, 12, 12);
        
        // Subtle outer glow on hover
        if (isHovered && isEnabled()) {
            g2.setStroke(new BasicStroke(4.0f));
            g2.setColor(new Color(borderCol.getRed(), borderCol.getGreen(), borderCol.getBlue(), 40));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 12, 12);
        }
        
        // 3. Draw text label
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h + fm.getAscent()) / 2 - 2;
        
        if (!isEnabled()) {
            g2.setColor(Theme.TEXT_MUTED);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(text, tx, ty);
        
        g2.dispose();
    }
}