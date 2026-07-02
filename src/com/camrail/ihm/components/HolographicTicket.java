package com.camrail.ihm.components;

import com.camrail.ihm.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class HolographicTicket extends JPanel {
    private String ticketNumber = "CAM-XXXX-XXXXXX";
    private String dateEmission = "01/01/2035";
    private String routeStr = "Douala ➔ Yaoundé";
    private String seatStr = "Siège 00 (Classique)";
    private String passagerName = "PASSAGER INCONNU";
    private String cniNumber = "000000000";
    private double totalPrice = 0.0;
    private String securityCode = "SEC-XXXX";
    
    // Fake barcode and QR code pattern
    private boolean[][] qrMatrix = new boolean[15][15];
    private int[] barcodeWidths = new int[35];

    public HolographicTicket() {
        setOpaque(false);
        setPreferredSize(new Dimension(480, 360));
        setMinimumSize(new Dimension(450, 320));
        
        generateCodes();
    }

    private void generateCodes() {
        Random rand = new Random();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                qrMatrix[i][j] = rand.nextBoolean();
                // Ensure alignment markers
                if ((i < 4 && j < 4) || (i > 10 && j < 4) || (i < 4 && j > 10)) {
                    qrMatrix[i][j] = (i == 0 || i == 3 || j == 0 || j == 3 || (i == 1.5 && j == 1.5));
                }
            }
        }
        
        for (int i = 0; i < barcodeWidths.length; i++) {
            barcodeWidths[i] = rand.nextInt(3) + 1; // Width of 1 to 3 pixels
        }
    }

    public void setTicketData(String ticketNum, String date, String route, String seat, String name, String cni, double price, String code) {
        this.ticketNumber = ticketNum != null ? ticketNum : "CAM-XXXX-XXXXXX";
        this.dateEmission = date != null ? date : "01/01/2035";
        this.routeStr = route != null ? route : "Douala ➔ Yaoundé";
        this.seatStr = seat != null ? seat : "Siège 00 (Classique)";
        this.passagerName = name != null ? name.toUpperCase() : "PASSAGER INCONNU";
        this.cniNumber = cni != null ? cni : "000000000";
        this.totalPrice = price;
        this.securityCode = code != null ? code : "SEC-XXXX";
        
        generateCodes();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw Main Ticket Glass Body
        Theme.drawGlassPanel(g2, 0, 0, w, h, 20);
        Theme.drawNeonBorder(g2, 0, 0, w, h, 20, Theme.NEON_PURPLE, Theme.NEON_CYAN);

        // 2. Draw Perforated Line (Tear-off stub indicator)
        int stubX = w - 130;
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 6.0f}, 0.0f));
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 80));
        g2.drawLine(stubX, 10, stubX, h - 10);

        // Draw top and bottom ticket notch cuts (classic boarding pass look)
        g2.setColor(new Color(8, 8, 16)); // match background
        g2.fill(new Ellipse2D.Double(stubX - 10, -10, 20, 20));
        g2.fill(new Ellipse2D.Double(stubX - 10, h - 10, 20, 20));

        // Border around notch cuts
        g2.setStroke(Theme.STROKE_1_2);
        g2.setColor(Theme.NEON_CYAN);
        g2.draw(new Arc2D.Double(stubX - 10, -10, 20, 20, 180, 180, Arc2D.OPEN));
        g2.draw(new Arc2D.Double(stubX - 10, h - 10, 20, 20, 0, 180, Arc2D.OPEN));

        // 3. Render Main Coupon (Left Side)
        int leftW = stubX - 20;

        // Header Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Theme.TEXT_LIGHT);
        g2.drawString("BOARDING PASS HOLOGRAPHIQUE", 20, 35);
        
        g2.setFont(new Font("Monospaced", Font.BOLD, 9));
        g2.setColor(Theme.NEON_CYAN);
        g2.drawString("ID: " + ticketNumber, 20, 52);

        // Departure ➔ Arrival codes
        String depCode = "DLA";
        String arrCode = "YDE";
        
        if (routeStr.contains("Yaoundé") && routeStr.contains("Douala")) {
            if (routeStr.indexOf("Yaoundé") < routeStr.indexOf("Douala")) {
                depCode = "YDE";
                arrCode = "DLA";
            }
        } else if (routeStr.contains("Bafoussam")) {
            arrCode = "BAF";
        } else if (routeStr.contains("Ngaoundéré")) {
            arrCode = "NGE";
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g2.setColor(Color.WHITE);
        g2.drawString(depCode, 20, 95);
        
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(Theme.NEON_CYAN);
        g2.drawString("➔", 88, 92);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g2.setColor(Color.WHITE);
        g2.drawString(arrCode, 120, 95);

        // Journey full label
        g2.setFont(Theme.FONT_BODY);
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString(routeStr, 20, 115);

        // Passenger Details Grid Layout
        int gridY = 150;
        int rowH = 38;
        
        // Passager
        drawTicketField(g2, 20, gridY, "PASSAGER BIOMÉTRIQUE", passagerName, Theme.TEXT_LIGHT);
        // CNI
        drawTicketField(g2, 175, gridY, "N° IDENTIFICATION CNI", cniNumber, Theme.TEXT_LIGHT);
        
        // Cabine Seat
        drawTicketField(g2, 20, gridY + rowH, "POSITION TRANSIT", seatStr, Theme.NEON_CYAN);
        // Date
        drawTicketField(g2, 175, gridY + rowH, "DATE ÉMISSION", dateEmission, Theme.TEXT_MUTED);

        // Draw Fake Barcode on the bottom left
        int barcodeX = 20;
        int barcodeY = h - 55;
        g2.setColor(Color.WHITE);
        g2.fillRect(barcodeX, barcodeY, 200, 30);
        
        g2.setColor(Color.BLACK);
        int currX = barcodeX + 8;
        for (int wIdx : barcodeWidths) {
            g2.fillRect(currX, barcodeY + 3, wIdx, 24);
            currX += wIdx + 2;
            if (currX > barcodeX + 190) break;
        }

        // 4. Render Stub Coupon (Right Side)
        int rightX = stubX + 15;

        // Micro-QR Code on stub
        int qrSize = 65;
        int qrX = rightX + (100 - qrSize) / 2;
        int qrY = 25;
        
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(qrX - 4, qrY - 4, qrSize + 8, qrSize + 8, 6, 6);
        
        g2.setColor(Color.BLACK);
        int pxSz = qrSize / 15;
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                if (qrMatrix[r][c]) {
                    g2.fillRect(qrX + c * pxSz, qrY + r * pxSz, pxSz, pxSz);
                }
            }
        }

        // Stub travel code details
        int stubDetailsY = 120;
        drawTicketField(g2, rightX, stubDetailsY, "CABINE", seatStr.contains("(") ? seatStr.substring(0, seatStr.indexOf("(")).trim() : seatStr, Theme.TEXT_LIGHT);
        drawTicketField(g2, rightX, stubDetailsY + 38, "SÉCURITÉ", securityCode, Theme.NEON_CYAN);
        
        // Price Badge on Stub
        drawTicketField(g2, rightX, stubDetailsY + 76, "TARIF TRANSIT", String.format("%,.0f CFA", totalPrice), Theme.NEON_PINK);

        // Security stamp watermark "CAMRAIL MAGLEV" (angled text)
        g2.rotate(Math.toRadians(-25), w - 50, h - 50);
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(Theme.getGlowColor(Theme.NEON_CYAN, 40));
        g2.drawString("SECURE VERIFIED", w - 120, h - 35);
        
        g2.dispose();
    }

    private void drawTicketField(Graphics2D g2, int x, int y, String label, String value, Color valColor) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 8));
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString(label, x, y);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(valColor);
        g2.drawString(value, x, y + 15);
    }
}
