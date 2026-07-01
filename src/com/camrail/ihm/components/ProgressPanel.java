package com.camrail.ihm.components;

import javax.swing.*;
import java.awt.*;

public class ProgressPanel extends JPanel {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel iconLabel;
    
    public ProgressPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        iconLabel = new JLabel("🖨️", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 102, 204));
        progressBar.setPreferredSize(new Dimension(400, 30));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        statusLabel = new JLabel("En attente...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(80, 80, 80));
    }
    
    private void setupLayout() {
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(iconLabel);
        centerPanel.add(progressBar);
        centerPanel.add(statusLabel);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    public void setProgress(int value) {
        progressBar.setValue(value);
        if (value == 100) {
            progressBar.setForeground(new Color(0, 153, 0));
        } else {
            progressBar.setForeground(new Color(0, 102, 204));
        }
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void setIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
    }
    
    public void reset() {
        setProgress(0);
        setStatus("En attente...");
        progressBar.setForeground(new Color(0, 102, 204));
        iconLabel.setText("🖨️");
    }
    
    public void setIcon(String icon) {
        iconLabel.setText(icon);
    }
    
    public void setSuccess() {
        setProgress(100);
        setStatus("✅ Impression terminée avec succès !");
        iconLabel.setText("✅");
        progressBar.setForeground(new Color(0, 153, 0));
    }
    
    public void setError(String message) {
        setStatus("❌ " + message);
        iconLabel.setText("❌");
        progressBar.setForeground(new Color(200, 50, 50));
    }
}