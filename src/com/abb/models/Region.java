package com.abb.models;

import com.abb.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Region {
    private int idRegion;
    private String libelleRegion;

    public int getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(int idRegion) {
        this.idRegion = idRegion;
    }

    public String getLibelleRegion() {
        return libelleRegion;
    }

    public void setLibelleRegion(String libelleRegion) {
        this.libelleRegion = libelleRegion;
    }

    public Region(int idRegion, String libelleRegion) {
        this.idRegion = idRegion;
        this.libelleRegion = libelleRegion;
    }

    public static void createTableIfNotExist() {
        Connection connection = DatabaseConnection.getConnection();
        Statement statement = null;
        try {
            if (connection != null) {
                statement = connection.createStatement();
                String createTableSQL = "CREATE TABLE IF NOT EXISTS Region (" +
                        "idRegion INT PRIMARY KEY, " +
                        "libelleRegion VARCHAR(255))";
                statement.executeUpdate(createTableSQL);
                System.out.println("Table 'Region' created or already exists.");
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
