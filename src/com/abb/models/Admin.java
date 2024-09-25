package com.abb.models;

import com.abb.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Admin {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin"; // Vous pouvez le changer

    // Méthode pour vérifier si la table Admin existe et la créer si nécessaire
    private static void createTableIfNotExists() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Admin (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(255) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vérifier si l'admin existe
    public static boolean checkAdminExists() {
        createTableIfNotExists();  // Appel possible créer d'abord
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM Admin WHERE username=?");
            pst.setString(1, USERNAME);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Créez l'administrateur s'il n'existe pas
    public static void createAdminIfNotExists() {
        createTableIfNotExists();  // Appel Create-table préiet initial logical assignment
        if (!checkAdminExists()) {
            try {
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pst = connection.prepareStatement("INSERT INTO Admin (username, password) VALUES (?, ?)");
                pst.setString(1, USERNAME);
                pst.setString(2, PASSWORD);
                pst.executeUpdate();
                System.out.println("Compte administrateur créé.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("L'administrateur existe déjà.");
        }
    }
}