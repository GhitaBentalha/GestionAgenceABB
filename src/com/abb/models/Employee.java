package com.abb.models;

import com.abb.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Employee {

    private int idEmploye;
    private String nom;
    private String prenom;
    private int age;
    private String numeroTelephone;
    private String poste;
    private int idAgence;
    private String cin;

    // Constructeurs, getters et setters

    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public int getIdAgence() {
        return idAgence;
    }

    public void setIdAgence(int idAgence) {
        this.idAgence = idAgence;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public Employee(int idEmploye, String nom, String prenom, int age, String numeroTelephone, String poste, int idAgence, String cin) {
        this.idEmploye = idEmploye;
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.numeroTelephone = numeroTelephone;
        this.poste = poste;
        this.idAgence = idAgence;
        this.cin = cin;
    }

    public static void createTableIfNotExist() {
        Connection connection = DatabaseConnection.getConnection();
        Statement statement = null;
        try {
            if (connection != null) {
                statement = connection.createStatement();
                String createTableSQL = "CREATE TABLE IF NOT EXISTS Employee (" +
                        "idEmploye INT PRIMARY KEY, " +
                        "nom VARCHAR(255), " +
                        "prenom VARCHAR(255), " +
                        "age INT, " +
                        "numeroTelephone VARCHAR(20), " +
                        "poste VARCHAR(255), " +
                        "idAgence INT, " +
                        "cin VARCHAR(20))";
                statement.executeUpdate(createTableSQL);
                System.out.println("Table 'Employee' created or already exists.");
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
