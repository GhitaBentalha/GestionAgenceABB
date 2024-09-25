package com.abb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ABB";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Aucun mot de passe

    private static Connection connection;

    // Méthode pour établir la connexion
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Charger le driver JDBC MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Établir la connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion réussie à la base de données.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur de connexion à la base de données.");
        }
        return connection;
    }

    // Méthode pour fermer la connexion
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la fermeture de la connexion.");
            }
        }
      }
 
    public static boolean connect() {

        try {

            // Charger le driver JDBC MySQL

            Class.forName("com.mysql.cj.jdbc.Driver");



            // Établir la connexion

            String url = "jdbc:mysql://localhost:3306/ABB";

            String user = "root";

            String password = ""; // Aucun mot de passe

            connection = DriverManager.getConnection(url, user, password);



            System.out.println("Connexion réussie à la base de données.");

            return true;

        } catch (ClassNotFoundException | SQLException e) {

            e.printStackTrace();

            return false;

        }
    }
}
