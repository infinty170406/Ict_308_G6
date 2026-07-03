package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SiegeGrid extends JPanel {
    private int rows = 8;
    private int cols = 4;
    private SiegeSelectionListener listener;
    private List<SiegeInfo> siegeInfos;
    private int totalPlaces = 32;
    private String selectedClasse = "ECONOMIQUE";
    
    // Hover and selection state
    private int hoveredIndex = -1;
    private int selectedIndex = -1;
    private Point mousePoint = new Point(0, 0);

    // Mini map train states
    private int activeWagon = 2; // 3rd wagon active

    public interface SiegeSelectionListener {
        void onSiegeSelected(int siegeNumber);
    }

    public SiegeGrid() {
        siegeInfos = new ArrayList<>();
        setOpaque(false);
        setPreferredSize(new Dimension(420, 520));
        setMinimumSize(new Dimension(400, 500));
        
        setupMouseListeners();
        buildGrid(totalPlaces);
    }

    public void setListener(SiegeSelectionListener listener) {
        this.listener = listener;
    }

    public void buildGrid(int totalPlaces) {
        this.totalPlaces = totalPlaces;
        
        // Define grid structure (4 columns)
        this.cols = 4;
        this.rows = (int) Math.ceil((double) totalPlaces / cols);
        if (rows < 6) rows = 6;
        
        rebuildSeatData();
    }

    private void rebuildSeatData() {
        siegeInfos.clear();
        Random random = new Random();
        int numero = 1;
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (numero > totalPlaces) break;
                
                // Determine seat class
                String type = "CLASSIQUE";
                if (r < 2) type = "VIP";
                else if (r < 4) type = "PREMIUM";
                
                // Simulat occupied seats (25%)
                boolean occupe = random.nextDouble() < 0.25;
                
                siegeInfos.add(new SiegeInfo(numero, type, occupe, r, c));
                numero++;
            }
        }
        
        selectedIndex = -1;
        hoveredIndex = -1;
        repaint();
    }

    public void updateForTrajet(int totalPlaces, String classe) {
        this.selectedClasse = classe;
        buildGrid(totalPlaces);
        updateSieges(classe);
    }

    public void updateSieges(String classe) {
        this.selectedClasse = classe;
        
        // Check compatible seats
        for (SiegeInfo info : siegeInfos) {
            boolean correspond = false;
            if ("ECONOMIQUE".equals(classe)) {
                correspond = "CLASSIQUE".equals(info.type);
            } else if ("BUSINESS".equals(classe)) {
                correspond = "CLASSIQUE".equals(info.type) || "PREMIUM".equals(info.type);
            } else if ("PREMIERE".equals(classe)) {
                correspond = true;
            }
            info.isCompatibleClass = correspond;
        }
        selectedIndex = -1;
        hoveredIndex = -1;
        repaint();
    }

    public Object getSiege(int numero) {
        for (SiegeInfo info : siegeInfos) {
            if (info.numero == numero) {
                return info;
            }
        }
        return null;
    }

    public void resetSelection() {
        selectedIndex = -1;
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            resetSelection();
        }
    }

    private void setupMouseListeners() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isEnabled()) return;
                
                mousePoint = e.getPoint();
                int prevHover = hoveredIndex;
                hoveredIndex = getSeatIndexAt(e.getX(), e.getY());
                
                if (prevHover != hoveredIndex) {
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isEnabled()) return;
                
                int index = getSeatIndexAt(e.getX(), e.getY());
                if (index != -1) {
                    SiegeInfo info = siegeInfos.get(index);
                    if (info.occupe || !info.isCompatibleClass) return; // Cannot select
                    
                    selectedIndex = index;
                    if (listener != null) {
                        listener.onSiegeSelected(info.numero);
                    }
                    repaint();
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    /**
     * Symmetrical and centered seat grid calculations.
     * Starts lower to avoid "too high" placement.
     */
    private Rectangle2D.Double getSeatBounds(int row, int col, int w, int h) {
        int seatW = 34;
        int seatH = 26;
        int spacingX = 10;
        int spacingY = 5;
        int aisleW = 24;
        
        // Total width of the grid of 4 columns (2 groups of 2 separated by aisle)
        int totalGridW = (seatW * 4) + (spacingX * 2) + aisleW;
        int startX = (w - totalGridW) / 2;
        
        // Offset Y position to start lower down the wagon
        int startY = 90; 
        
        // Calculate X based on column groups
        int x = startX;
        if (col == 0) {
            x = startX;
        } else if (col == 1) {
            x = startX + seatW + spacingX;
        } else if (col == 2) {
            x = startX + seatW * 2 + spacingX + aisleW;
        } else if (col == 3) {
            x = startX + seatW * 3 + spacingX * 2 + aisleW;
        }
        
        int y = startY + row * (seatH + spacingY);
        
        return new Rectangle2D.Double(x, y, seatW, seatH);
    }

    private int getSeatIndexAt(int px, int py) {
        int w = getWidth();
        int h = getHeight();
        for (int i = 0; i < siegeInfos.size(); i++) {
            SiegeInfo info = siegeInfos.get(i);
            Rectangle2D.Double bounds = getSeatBounds(info.row, info.col, w, h);
            if (bounds.contains(px, py)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);
        
        int w = getWidth();
        int h = getHeight();
        
        // 1. Draw Wagon Container (Minimalist, clean, structured)
        int wagonX = 35;
        int wagonY = 55;
        int wagonW = w - 70;
        int wagonH = h - 70;
        
        // Wagon body backdrop (Solid, high-quality translucent panel)
        g2.setColor(Theme.BG_GLASS);
        g2.fillRoundRect(wagonX, wagonY, wagonW, wagonH, 20, 20);
        
        // Clean neon border
        Theme.drawNeonBorder(g2, wagonX, wagonY, wagonW, wagonH, 20, Theme.getGlowColor(Theme.NEON_CYAN, 80), Theme.getGlowColor(Theme.NEON_BLUE, 50));
        
        // Symmetrical Door indicators (very clean)
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 100));
        g2.fillRect(w / 2 - 25, wagonY, 50, 4); 
        g2.fillRect(w / 2 - 25, wagonY + wagonH - 4, 50, 4);

        // 2. Draw Mini Train Blueprint (Top of the panel)
        int trainX = w / 2 - 80;
        int trainY = 15;
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString("PLAN DU TRAIN", trainX - 5, trainY);
        
        for (int i = 0; i < 5; i++) {
            int wx = trainX + (i * 32);
            int wy = trainY + 8;
            int ww = 26;
            int wh = 12;
            
            if (i == activeWagon) {
                g2.setColor(Theme.NEON_CYAN);
                g2.fillRoundRect(wx, wy, ww, wh, 4, 4);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Monospaced", Font.BOLD, 8));
                g2.drawString(String.valueOf(i+1), wx + 10, wy + 9);
            } else {
                g2.setColor(Theme.getGlowColor(Color.WHITE, 30));
                g2.fillRoundRect(wx, wy, ww, wh, 4, 4);
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 8));
                g2.drawString(String.valueOf(i+1), wx + 10, wy + 9);
            }
            
            if (i < 4) {
                g2.setColor(Theme.getGlowColor(Color.WHITE, 40));
                g2.drawLine(wx + ww, wy + wh/2, wx + ww + 6, wy + wh/2);
            }
        }
        
        // 3. Draw Interior Cabin Title/Status
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(Theme.TEXT_LIGHT);
        g2.drawString("CABINE PASSAGER - CLASSE : " + selectedClasse, wagonX + 20, wagonY + 25);

        // 4. Render Seats (Clean, symmetrical, no complicated transformations)
        for (int i = 0; i < siegeInfos.size(); i++) {
            SiegeInfo info = siegeInfos.get(i);
            Rectangle2D.Double bounds = getSeatBounds(info.row, info.col, w, h);
            
            boolean isHovered = (i == hoveredIndex);
            boolean isSelected = (i == selectedIndex);
            
            // Seat Color configuration
            Color seatColor;
            Color borderColor;
            
            if (info.occupe) {
                // Occupied seat (Clean red/pink outline, dark filled center)
                seatColor = new Color(255, 0, 80, 20);
                borderColor = Theme.NEON_PINK;
            } else if (!info.isCompatibleClass) {
                // Disabled seat
                seatColor = new Color(45, 45, 55, 40);
                borderColor = new Color(75, 75, 85, 90);
            } else if (isSelected) {
                // Selected seat (Vibrant purple fill)
                seatColor = Theme.NEON_PURPLE;
                borderColor = Color.WHITE;
            } else if (isHovered) {
                // Hovered seat
                seatColor = Theme.getGlowColor(Theme.NEON_CYAN, 80);
                borderColor = Theme.NEON_CYAN;
            } else {
                // Available based on class
                if ("VIP".equals(info.type)) {
                    seatColor = new Color(255, 215, 0, 15);
                    borderColor = new Color(255, 215, 0, 130);
                } else if ("PREMIUM".equals(info.type)) {
                    seatColor = new Color(0, 110, 255, 15);
                    borderColor = Theme.NEON_BLUE;
                } else {
                    seatColor = new Color(0, 240, 255, 10);
                    borderColor = Theme.getGlowColor(Theme.NEON_CYAN, 90);
                }
            }
            
            // Draw clean rounded seat
            g2.setColor(seatColor);
            g2.fillRoundRect((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height, 8, 8);
            
            g2.setColor(borderColor);
            g2.setStroke(isSelected || isHovered ? Theme.STROKE_2 : Theme.STROKE_1_2);
            g2.drawRoundRect((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height, 8, 8);
            
            // Minimal armrest accents
            g2.fillRect((int)bounds.x - 1, (int)bounds.y + 7, 2, 8);
            g2.fillRect((int)bounds.x + (int)bounds.width - 1, (int)bounds.y + 7, 2, 8);
            
            // Draw Seat Number
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            g2.setColor(isSelected ? Color.WHITE : (info.occupe ? Theme.NEON_PINK : Theme.TEXT_LIGHT));
            
            String numStr = String.valueOf(info.numero);
            FontMetrics fm = g2.getFontMetrics();
            int nx = (int)(bounds.x + (bounds.width - fm.stringWidth(numStr)) / 2);
            int ny = (int)(bounds.y + (bounds.height + fm.getAscent()) / 2 - 2);
            g2.drawString(numStr, nx, ny);
        }
        
        // 5. Draw Clean, Centered Tooltip on Hover
        if (hoveredIndex != -1) {
            SiegeInfo hInfo = siegeInfos.get(hoveredIndex);
            
            int tipW = 150;
            int tipH = 75;
            int tipX = mousePoint.x + 12;
            int tipY = mousePoint.y - 12;
            
            // Contain tooltip inside
            if (tipX + tipW > w) tipX = mousePoint.x - tipW - 12;
            if (tipY + tipH > h) tipY = mousePoint.y - tipH - 5;
            if (tipY < 0) tipY = 5;
            
            g2.translate(tipX, tipY);
            
            // Tooltip background
            g2.setColor(new Color(12, 12, 24, 240));
            g2.fillRoundRect(0, 0, tipW, tipH, 10, 10);
            
            g2.setColor(Theme.NEON_CYAN);
            g2.setStroke(Theme.STROKE_1_2);
            g2.drawRoundRect(0, 0, tipW, tipH, 10, 10);
            
            // Texts
            g2.setColor(Theme.TEXT_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.drawString("SIÈGE N° " + hInfo.numero, 12, 20);
            
            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            String status = hInfo.occupe ? "OCCUPÉ" : "DISPONIBLE";
            Color statusCol = hInfo.occupe ? Theme.NEON_PINK : Theme.NEON_CYAN;
            if (!hInfo.isCompatibleClass) {
                status = "NON ASSIGNABLE";
                statusCol = Theme.TEXT_MUTED;
            }
            g2.setColor(statusCol);
            g2.drawString(status, 12, 34);
            
            g2.setFont(Theme.FONT_BODY);
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString("Classe: " + hInfo.type, 12, 48);
            
            int basePrice = 5000;
            if ("VIP".equals(hInfo.type)) basePrice += 3000;
            else if ("PREMIUM".equals(hInfo.type)) basePrice += 1000;
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(Theme.NEON_PINK);
            g2.drawString("Tarif: +" + (basePrice - 5000) + " CFA", 12, 62);
            
            g2.translate(-tipX, -tipY);
        }
        
        g2.dispose();
    }

    public class SiegeInfo {
        public int numero;
        public String type;
        public boolean occupe;
        public int row;
        public int col;
        public boolean isCompatibleClass = true;
        
        public SiegeInfo(int numero, String type, boolean occupe, int row, int col) {
            this.numero = numero;
            this.type = type;
            this.occupe = occupe;
            this.row = row;
            this.col = col;
        }
        
        @Override
        public String toString() {
            return "Siège " + numero + " (" + type + ")";
        }
    }
}