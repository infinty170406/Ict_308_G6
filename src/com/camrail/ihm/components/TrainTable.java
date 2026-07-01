package com.camrail.ihm.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrainTable extends JTable {
    private DefaultTableModel model;
    
    public TrainTable() {
        String[] columns = {"Départ", "Arrivée", "Heure", "Durée", "Prix (CFA)", "Places"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setModel(model);
        
        // Configuration du style
        setRowHeight(35);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        getTableHeader().setBackground(new Color(0, 51, 102));
        getTableHeader().setForeground(Color.WHITE);
        getTableHeader().setReorderingAllowed(false);
        
        setSelectionBackground(new Color(0, 102, 204, 50));
        setSelectionForeground(Color.BLACK);
        setGridColor(new Color(200, 200, 200));
        setShowVerticalLines(true);
        setShowHorizontalLines(true);
        
        // Ajuster la largeur des colonnes
        getColumnModel().getColumn(0).setPreferredWidth(120);
        getColumnModel().getColumn(1).setPreferredWidth(120);
        getColumnModel().getColumn(2).setPreferredWidth(80);
        getColumnModel().getColumn(3).setPreferredWidth(70);
        getColumnModel().getColumn(4).setPreferredWidth(100);
        getColumnModel().getColumn(5).setPreferredWidth(70);
    }
    
    public void loadTrajets() {
        model.setRowCount(0);
        // Chaque trajet a maintenant un nombre de places cohérent
        Object[][] data = {
            {"Douala", "Yaoundé", "07:00", "4h", "5000", "45"},
            {"Yaoundé", "Douala", "08:30", "4h", "5000", "45"},
            {"Douala", "Bafoussam", "09:15", "3h", "4000", "32"},
            {"Bafoussam", "Douala", "11:00", "3h", "4000", "32"},
            {"Douala", "Ngaoundéré", "13:45", "6h", "7500", "50"},
            {"Ngaoundéré", "Douala", "15:30", "6h", "7500", "50"},
            {"Yaoundé", "Bafoussam", "10:00", "2h30", "3500", "25"},
            {"Bafoussam", "Yaoundé", "14:00", "2h30", "3500", "25"}
        };
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    public Object getTrajetAt(int row) {
        if (row >= 0 && row < model.getRowCount()) {
            final int currentRow = row;
            return new Object() {
                private String depart = (String) model.getValueAt(currentRow, 0);
                private String arrivee = (String) model.getValueAt(currentRow, 1);
                private String heure = (String) model.getValueAt(currentRow, 2);
                private String duree = (String) model.getValueAt(currentRow, 3);
                private String prix = (String) model.getValueAt(currentRow, 4);
                private String places = (String) model.getValueAt(currentRow, 5);
                
                @Override
                public String toString() {
                    return depart + " → " + arrivee + " (" + heure + ") - " + places + " places";
                }
                
                public int getPlaces() {
                    return Integer.parseInt(places);
                }
                
                public double getPrix() {
                    return Double.parseDouble(prix.replaceAll(",", ""));
                }
            };
        }
        return null;
    }
    
    public int getPlacesAt(int row) {
        if (row >= 0 && row < model.getRowCount()) {
            String placesStr = (String) model.getValueAt(row, 5);
            return Integer.parseInt(placesStr);
        }
        return 0;
    }
    
    public double getTrajetPrice(int row) {
        if (row >= 0 && row < model.getRowCount()) {
            String priceStr = (String) model.getValueAt(row, 4);
            return Double.parseDouble(priceStr.replaceAll(",", ""));
        }
        return 0;
    }
}