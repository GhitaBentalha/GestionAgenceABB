package com.abb.main;

import com.abb.ui.Login;
import com.abb.db.DatabaseConnection;
import com.abb.models.Agency;
import com.abb.models.Region;
import com.abb.models.Ville;
import com.abb.models.Employee;

import java.net.MalformedURLException;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Établir la connexion à la base de données MySQL
        DatabaseConnection dbConnection = new DatabaseConnection();
        boolean isConnected = dbConnection.connect();

        if (!isConnected) {
            System.err.println("Erreur: Impossible de se connecter à la base de données.");
            return;  // Arrêter le programme si la connexion échoue
        }

        // Créer les tables si elles n'existent pas
        Agency.createTableIfNotExist();
        Region.createTableIfNotExist();
        Ville.createTableIfNotExist();
        Employee.createTableIfNotExist();

        // Initialiser l'interface utilisateur Swing sur le thread d'interface utilisateur
        SwingUtilities.invokeLater(() -> {
            Login mainWindow = null;
            try {
                mainWindow = new Login();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            mainWindow.setVisible(true);
        });
    }
}
