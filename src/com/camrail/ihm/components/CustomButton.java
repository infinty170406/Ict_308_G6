package com.camrail.ihm.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;
    
    public CustomButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setBackground(baseColor);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(true);
        setPreferredSize(new Dimension(150, 45));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effets de survol
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(baseColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
            }
        });
    }
    
    public void setBaseColor(Color color) {
        this.baseColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        setBackground(baseColor);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackground(baseColor);
        } else {
            setBackground(Color.GRAY);
        }
    }
}