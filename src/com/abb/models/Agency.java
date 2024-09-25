package com.abb.models;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.abb.db.DatabaseConnection;

public class Agency {
    private int idAgence;
    private String nomAgence;
    private String numeroTelephone;
    private String numeroFax;
    private int codePostal;
    private int idRegion;
    private String adresse; // Nouveau champ pour l'adresse

    // Constructeurs, getters et setters
    
    public int getIdAgence() {
        return idAgence;
    }

    public void setIdAgence(int idAgence) {
        this.idAgence = idAgence;
    }

    public String getNomAgence() {
        return nomAgence;
    }

    public void setNomAgence(String nomAgence) {
        this.nomAgence = nomAgence;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getNumeroFax() {
        return numeroFax;
    }

    public void setNumeroFax(String numeroFax) {
        this.numeroFax = numeroFax;
    }

    public int getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public int getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(int idRegion) {
        this.idRegion = idRegion;
    }

    public String getAdresse() { // Getter pour l'adresse
        return adresse;
    }

    public void setAdresse(String adresse) { // Setter pour l'adresse
        this.adresse = adresse;
    }

    public Agency(int idAgence, String nomAgence, String numeroTelephone, String numeroFax, int codePostal,
                  int idRegion, String adresse) {
        this.idAgence = idAgence;
        this.nomAgence = nomAgence;
        this.numeroTelephone = numeroTelephone;
        this.numeroFax = numeroFax;
        this.codePostal = codePostal;
        this.idRegion = idRegion;
        this.adresse = adresse; // Initialiser le champ adresse
    }

    // Méthode pour créer la table si elle n'existe pas
    public static void createTableIfNotExist() {
        Connection connection = DatabaseConnection.getConnection();
        
        if (connection != null) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS agences ("
                    + "idAgence INT AUTO_INCREMENT PRIMARY KEY, "
                    + "nomAgence VARCHAR(100), "
                    + "numeroTelephone VARCHAR(20), "
                    + "numeroFax VARCHAR(20), "
                    + "codePostal INT, "
                    + "idRegion INT, "
                    + "adresse VARCHAR(255))"; // Ajouter la colonne adresse
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
                System.out.println("La table 'agences' a été créée ou existe déjà.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la création de la table 'agences' : " + e.getMessage());
            }
        } else {
            System.out.println("La connexion à la base de données n'est pas établie.");
        }
    }
}
