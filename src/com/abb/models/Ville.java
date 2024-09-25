package com.abb.models;

import com.abb.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Ville {
    private int idVille;
    private String libelleVille;
    private int idRegion;

    public int getIdVille() {
        return idVille;
    }

    public void setIdVille(int idVille) {
        this.idVille = idVille;
    }

    public String getLibelleVille() {
        return libelleVille;
    }

    public void setLibelleVille(String libelleVille) {
        this.libelleVille = libelleVille;
    }

    public int getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(int idRegion) {
        this.idRegion = idRegion;
    }

    public Ville(int idVille, String libelleVille, int idRegion) {
        this.idVille = idVille;
        this.libelleVille = libelleVille;
        this.idRegion = idRegion;
    }

    public static void createTableIfNotExist() {
        Connection connection = DatabaseConnection.getConnection();
        Statement statement = null;
        try {
            if (connection != null) {
                statement = connection.createStatement();
                String createTableSQL = "CREATE TABLE IF NOT EXISTS Ville (" +
                        "idVille INT PRIMARY KEY, " +
                        "libelleVille VARCHAR(255), " +
                        "idRegion INT, " +
                        "FOREIGN KEY (idRegion) REFERENCES Region(idRegion))";
                statement.executeUpdate(createTableSQL);
                System.out.println("Table 'Ville' created or already exists.");
            } else {
                System.out.println("Database connection is not established.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
