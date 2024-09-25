package com.abb.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.abb.db.DatabaseConnection;

public class AgencyManagement extends JFrame {

    private JTable agencyTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, backButton, searchButton;
    private JTextField searchField;
    private JComboBox<String> regionComboBox; // ComboBox pour les régions
    private Map<String, Integer> regionMap;   // Map pour stocker le nom de la région et son ID

    public AgencyManagement() {
        super("Al Barid Bank - Gestion des Agences");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
        loadRegions(); // Charger les régions d'abord
        loadAgencies(); // Ensuite charger les agences
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Barre de navigation
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(255, 205, 35)); // #ffcd23
        navPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Bouton Retour
        backButton = new JButton("Retour");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(189, 157, 126)); // #b5927f
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createLineBorder(new Color(189, 157, 126), 1));
        backButton.setOpaque(true);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> goToHomePage());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        navPanel.add(backButton, gbc);

        // Titre
        JLabel titleLabel = new JLabel("Gestion des Agences");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(189, 157, 126)); // #b5927f

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        navPanel.add(titleLabel, gbc);

        add(navPanel, BorderLayout.NORTH);

        // Panneau des contrôles
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BorderLayout());
        controlsPanel.setBackground(Color.WHITE);

        // Panneau des boutons (ajout, suppression)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        addButton = new JButton("Ajouter");
        deleteButton = new JButton("Supprimer");

        // Style des boutons
        JButton[] buttons = {addButton, deleteButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBackground(new Color(189, 157, 126)); // #b5927f
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(189, 157, 126), 1));
            button.setOpaque(true);
            button.setFocusPainted(false);
        }

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Panneau de recherche
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");

        // Style du bouton de recherche
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setBackground(new Color(189, 157, 126)); // #b5927f
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(189, 157, 126), 1));
        searchButton.setOpaque(true);
        searchButton.setFocusPainted(false);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Ajouter les panneaux de contrôles à la fenêtre
        controlsPanel.add(buttonPanel, BorderLayout.WEST);
        controlsPanel.add(searchPanel, BorderLayout.EAST);

        add(controlsPanel, BorderLayout.SOUTH);

        // Tableau des agences
        String[] columnNames = {"ID Agence", "Nom Agence", "Téléphone", "Fax", "Code Postal", "Région"};
        tableModel = new DefaultTableModel(columnNames, 0);
        agencyTable = new JTable(tableModel);
        agencyTable.setRowHeight(30);
        agencyTable.setGridColor(new Color(189, 157, 126)); // #b5927f
        agencyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        agencyTable.setSelectionBackground(new Color(255, 205, 35)); // #ffcd23

        JScrollPane scrollPane = new JScrollPane(agencyTable);
        add(scrollPane, BorderLayout.CENTER);

        // Actions des boutons
        addButton.addActionListener(e -> showAddAgencyDialog());
        deleteButton.addActionListener(e -> deleteAgency());
        searchButton.addActionListener(e -> searchAgencies());
    }

    private void loadAgencies() {
        if (regionMap == null) {
            JOptionPane.showMessageDialog(this, "Les régions doivent être chargées avant de charger les agences.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idAgence, nomAgence, numeroTelephone, numeroFax, codePostal, idRegion FROM agences";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // Effacer les données existantes

            while (resultSet.next()) {
                int id = resultSet.getInt("idAgence");
                String name = resultSet.getString("nomAgence");
                String phone = resultSet.getString("numeroTelephone");
                String fax = resultSet.getString("numeroFax");
                int postalCode = resultSet.getInt("codePostal");
                int regionId = resultSet.getInt("idRegion");

                // Obtenir le nom de la région depuis l'ID
                String regionName = getRegionNameById(regionId);

                tableModel.addRow(new Object[]{id, name, phone, fax, postalCode, regionName});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getRegionNameById(int idRegion) {
        if (regionMap == null) {
            return "Inconnu"; // Retourne "Inconnu" si la map est null
        }

        for (Map.Entry<String, Integer> entry : regionMap.entrySet()) {
            if (entry.getValue() == idRegion) {
                return entry.getKey();
            }
        }
        return "Inconnu"; // Retourne "Inconnu" si aucun nom de région ne correspond
    }

    private void loadRegions() {
        regionMap = new HashMap<>(); // Initialiser la map pour stocker les régions

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idRegion, libelleRegion FROM Region";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("idRegion");
                String name = resultSet.getString("libelleRegion");
                regionMap.put(name, id); // Associe le nom de la région à son ID
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des régions: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAgencyDialog() {
        JDialog dialog = new JDialog(this, "Ajouter Agence", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panneau du formulaire
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.setBackground(Color.WHITE);

        JTextField nameField = new JTextField(10);
        JTextField phoneField = new JTextField(10);
        JTextField faxField = new JTextField(10);
        JTextField postalCodeField = new JTextField(10);
        regionComboBox = new JComboBox<>(); // Utilisation de JComboBox pour les régions

        // Ajouter les noms des régions à la JComboBox
        if (regionMap != null) {
            for (String regionName : regionMap.keySet()) {
                regionComboBox.addItem(regionName);
            }
        }

        formPanel.add(new JLabel("Nom Agence:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Téléphone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Fax:"));
        formPanel.add(faxField);
        formPanel.add(new JLabel("Code Postal:"));
        formPanel.add(postalCodeField);
        formPanel.add(new JLabel("Région:"));
        formPanel.add(regionComboBox);

        // Panneau des boutons dans le dialogue
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        // Style des boutons
        JButton[] buttons = {saveButton, cancelButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBackground(new Color(189, 157, 126)); // #b5927f
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(189, 157, 126), 1));
            button.setOpaque(true);
            button.setFocusPainted(false);
        }

        saveButton.addActionListener(e -> addAgency(nameField.getText(), phoneField.getText(), faxField.getText(),
                postalCodeField.getText(), (String) regionComboBox.getSelectedItem(), dialog));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addAgency(String name, String phone, String fax, String postalCode, String regionName, JDialog dialog) {
        Integer regionId = regionMap.get(regionName);

        if (regionId == null) {
            JOptionPane.showMessageDialog(this, "Région invalide sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO agences (nomAgence, numeroTelephone, numeroFax, codePostal, idRegion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, fax);
            statement.setInt(4, Integer.parseInt(postalCode));
            statement.setInt(5, regionId);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Agence ajoutée avec succès!");
                loadAgencies(); // Recharger les agences après ajout
                dialog.dispose();
            }

            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur d'ajout de l'agence: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAgency() {
        int selectedRow = agencyTable.getSelectedRow();

        if (selectedRow >= 0) {
            int agencyId = (Integer) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette agence ?", "Confirmer", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String query = "DELETE FROM agences WHERE idAgence = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, agencyId);
                    int rowsDeleted = statement.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Agence supprimée avec succès!");
                        loadAgencies(); // Recharger les agences après suppression
                    }

                    statement.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Erreur de suppression de l'agence: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une agence à supprimer.", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchAgencies() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadAgencies(); // Si le champ de recherche est vide, recharge toutes les agences
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idAgence, nomAgence, numeroTelephone, numeroFax, codePostal, idRegion " +
                           "FROM agences WHERE nomAgence LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // Effacer les données existantes

            while (resultSet.next()) {
                int id = resultSet.getInt("idAgence");
                String name = resultSet.getString("nomAgence");
                String phone = resultSet.getString("numeroTelephone");
                String fax = resultSet.getString("numeroFax");
                int postalCode = resultSet.getInt("codePostal");
                int regionId = resultSet.getInt("idRegion");

                // Obtenir le nom de la région depuis l'ID
                String regionName = getRegionNameById(regionId);

                tableModel.addRow(new Object[]{id, name, phone, fax, postalCode, regionName});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de recherche des agences: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goToHomePage() {
        // Code pour revenir à la page d'accueil
        dispose(); // Ferme la fenêtre actuelle
        // Exemple: new HomePage().setVisible(true); // Remplacez par l'appel à la page d'accueil
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgencyManagement().setVisible(true));
    }
}
