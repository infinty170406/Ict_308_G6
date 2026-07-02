package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TrainTable extends JPanel {
    private DefaultListSelectionModel selectionModel;
    private List<TrajetData> trajets;
    private List<CardPanel> cardPanels;
    private int selectedRow = -1;

    public TrainTable() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trajets = new ArrayList<>();
        cardPanels = new ArrayList<>();
    }

    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void clearSelection() {
        selectedRow = -1;
        selectionModel.clearSelection();
        for (CardPanel card : cardPanels) {
            card.setSelected(false);
        }
        repaint();
    }

    public void loadTrajets() {
        trajets.clear();
        cardPanels.clear();
        removeAll();

        // Sample trajet data
        trajets.add(new TrajetData("Douala", "Yaoundé", "07:00", "4h", 5000, 45, 100, "☀️ 29°C"));
        trajets.add(new TrajetData("Yaoundé", "Douala", "08:30", "4h", 5000, 15, 100, "☀️ 27°C"));
        trajets.add(new TrajetData("Douala", "Bafoussam", "09:15", "3h", 4000, 32, 60, "☁️ 22°C"));
        trajets.add(new TrajetData("Bafoussam", "Douala", "11:00", "3h", 4000, 8, 60, "🌧️ 21°C"));
        trajets.add(new TrajetData("Douala", "Ngaoundéré", "13:45", "6h", 7500, 50, 120, "⚡ 25°C"));
        trajets.add(new TrajetData("Ngaoundéré", "Douala", "15:30", "6h", 7500, 11, 120, "☁️ 24°C"));
        trajets.add(new TrajetData("Yaoundé", "Bafoussam", "10:00", "2h30", 3500, 25, 60, "☀️ 26°C"));
        trajets.add(new TrajetData("Bafoussam", "Yaoundé", "14:00", "2h30", 3500, 0, 60, "🌧️ 20°C")); // Complete

        for (int i = 0; i < trajets.size(); i++) {
            TrajetData t = trajets.get(i);
            CardPanel card = new CardPanel(t, i);
            cardPanels.add(card);
            add(card);
            add(Box.createVerticalStrut(12));
        }

        revalidate();
        repaint();
    }

    public static class TrajetInfo {
        private String depart;
        private String arrivee;
        private String heure;
        private String duree;
        private double prix;
        private int places;

        public TrajetInfo(String depart, String arrivee, String heure, String duree, double prix, int places) {
            this.depart = depart;
            this.arrivee = arrivee;
            this.heure = heure;
            this.duree = duree;
            this.prix = prix;
            this.places = places;
        }

        @Override
        public String toString() {
            return depart + " → " + arrivee + " (" + heure + ") - " + places + " places";
        }

        public String getDepart() { return depart; }
        public String getArrivee() { return arrivee; }
        public String getHeure() { return heure; }
        public String getDuree() { return duree; }
        public double getPrix() { return prix; }
        public int getPlaces() { return places; }
    }

    public TrajetInfo getTrajetAt(int row) {
        if (row >= 0 && row < trajets.size()) {
            TrajetData t = trajets.get(row);
            return new TrajetInfo(t.depart, t.arrivee, t.heure, t.duree, t.prix, t.placesDispo);
        }
        return null;
    }

    public int getPlacesAt(int row) {
        if (row >= 0 && row < trajets.size()) {
            return trajets.get(row).placesDispo;
        }
        return 0;
    }

    public double getTrajetPrice(int row) {
        if (row >= 0 && row < trajets.size()) {
            return trajets.get(row).prix;
        }
        return 0;
    }

    // Interactive Card Panel
    private class CardPanel extends JPanel {
        private TrajetData data;
        private int index;
        private boolean isHovered = false;
        private boolean isSelected = false;

        public CardPanel(TrajetData data, int index) {
            this.data = data;
            this.index = index;
            setOpaque(false);
            setPreferredSize(new Dimension(380, 110));
            setMinimumSize(new Dimension(380, 110));
            setMaximumSize(new Dimension(450, 110));
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
                    selectCard();
                }
            });
        }

        public void setSelected(boolean sel) {
            this.isSelected = sel;
            repaint();
        }

        private void selectCard() {
            selectedRow = index;
            for (int i = 0; i < cardPanels.size(); i++) {
                cardPanels.get(i).setSelected(i == index);
            }
            selectionModel.setSelectionInterval(index, index);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Theme.applyQualityRendering(g2);

            int w = getWidth();
            int h = getHeight();

            // Background Card
            if (isSelected) {
                // Violet-blue glow for selection
                Theme.drawGlassPanel(g2, 0, 0, w, h, 16);
                Theme.drawNeonBorder(g2, 0, 0, w, h, 16, Theme.NEON_CYAN, Theme.NEON_PURPLE);
            } else if (isHovered) {
                // Subtle cyan hover border
                Theme.drawGlassPanel(g2, 0, 0, w, h, 16);
                Theme.drawNeonBorder(g2, 0, 0, w, h, 16, new Color(0, 240, 255, 120), new Color(0, 110, 255, 60));
            } else {
                // Standard Glassmorphism Card
                Theme.drawGlassPanel(g2, 0, 0, w, h, 16);
            }

            // Draw Trajet Texts
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(Theme.TEXT_LIGHT);
            g2.drawString(data.depart + "  ➔  " + data.arrivee, 20, 32);

            // Draw Class / Heure
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.setColor(Theme.NEON_CYAN);
            g2.drawString("DEP: " + data.heure + "  [" + data.duree + "]", 20, 56);

            // Météo Info
            g2.setFont(Theme.FONT_BODY);
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString(data.weather, 20, 80);

            // Price badge (top-right)
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.setColor(Theme.NEON_PINK);
            String priceStr = String.format("%,.0f CFA", data.prix);
            g2.drawString(priceStr, w - 120, 32);

            // Occupancy details (bottom-right)
            int total = data.totalPlaces;
            int dispo = data.placesDispo;
            int occupied = total - dispo;
            double fillRatio = (double) occupied / total;
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(Theme.TEXT_MUTED);
            String capStr = dispo + " / " + total + " places dispo";
            if (dispo == 0) {
                capStr = "COMPLET";
                g2.setColor(Theme.NEON_PINK);
            }
            g2.drawString(capStr, w - 145, 60);

            // Small occupancy progress bar
            int pBarW = 120;
            int pBarH = 6;
            int pBarX = w - 140;
            int pBarY = 72;

            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRoundRect(pBarX, pBarY, pBarW, pBarH, 4, 4);

            Color fillCol = Theme.NEON_CYAN;
            if (fillRatio > 0.8) fillCol = Theme.NEON_PINK;
            else if (fillRatio > 0.5) fillCol = Theme.NEON_PURPLE;

            g2.setColor(fillCol);
            g2.fillRoundRect(pBarX, pBarY, (int)(pBarW * fillRatio), pBarH, 4, 4);

            g2.dispose();
        }
    }

    private static class TrajetData {
        String depart;
        String arrivee;
        String heure;
        String duree;
        double prix;
        int placesDispo;
        int totalPlaces;
        String weather;

        public TrajetData(String depart, String arrivee, String heure, String duree, double prix, int placesDispo, int totalPlaces, String weather) {
            this.depart = depart;
            this.arrivee = arrivee;
            this.heure = heure;
            this.duree = duree;
            this.prix = prix;
            this.placesDispo = placesDispo;
            this.totalPlaces = totalPlaces;
            this.weather = weather;
        }
    }
}