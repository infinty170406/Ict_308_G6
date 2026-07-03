package com.camrail.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class GenerateurCode {
    public static String genererNumeroTicket() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CAR-" + date + "-" + random;
    }

    public static String genererCodeSecurite() {
        // Style: AZE-789-XYZ
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder();
        // 3 random letters
        for (int i = 0; i < 3; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        sb.append("-");
        // 3 random digits
        for (int i = 0; i < 3; i++) {
            sb.append(digits.charAt((int) (Math.random() * digits.length())));
        }
        sb.append("-");
        // 3 random letters
        for (int i = 0; i < 3; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}
