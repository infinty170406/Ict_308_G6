package com.camrail;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Lancement de Camrail Intercity - Modernisé en JavaFX ===");
        
        // Launch JavaFX Application passing the main App class
        Application.launch(com.camrail.ihm.javafx.CamrailJavaFXApp.class, args);
    }
}