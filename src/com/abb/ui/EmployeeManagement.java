package com.abb.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.abb.db.DatabaseConnection;

public class EmployeeManagement extends JFrame {

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, deleteButton, searchButton, backButton;

    public EmployeeManagement() {
        super("Al Barid Bank - Gestion des Employés");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
        loadEmployees();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Barre de navigation
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(255, 205, 35)); // #ffcd23
        navPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Marges autour des composants

        // Ajouter le bouton Retour
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
        gbc.anchor = GridBagConstraints.WEST; // Aligner à gauche
        navPanel.add(backButton, gbc);

        // Ajouter le titre
        JLabel titleLabel = new JLabel("Gestion d'Employés");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(189, 157, 126)); // #b5927f

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Permet au titre de prendre l'espace restant
        gbc.anchor = GridBagConstraints.CENTER; // Centrer le titre
        navPanel.add(titleLabel, gbc);

        add(navPanel, BorderLayout.NORTH);

        // Panneau principal des contrôles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Aligner les boutons à gauche
        controlPanel.setBackground(Color.WHITE);

        // Initialisation des boutons
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

        controlPanel.add(addButton);
        controlPanel.add(deleteButton);

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

        // Panneau principal des contrôles avec recherche
        JPanel mainControlPanel = new JPanel(new BorderLayout());
        mainControlPanel.setBackground(Color.WHITE);
        mainControlPanel.add(controlPanel, BorderLayout.CENTER);
        mainControlPanel.add(searchPanel, BorderLayout.EAST);

        add(mainControlPanel, BorderLayout.SOUTH);

        // Tableau des employés
        String[] columnNames = {"ID", "Nom", "Prénom", "Âge", "Téléphone", "Poste", "ID Agence", "CIN"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(30);
        employeeTable.setGridColor(new Color(189, 157, 126)); // #b5927f
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        employeeTable.setSelectionBackground(new Color(255, 205, 35)); // #ffcd23

        JTableHeader header = employeeTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(189, 157, 126)); // #b5927f
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Actions des boutons
        addButton.addActionListener(e -> showAddEmployeeDialog());
        deleteButton.addActionListener(e -> deleteEmployee());
        searchButton.addActionListener(e -> searchEmployee());
    }

    private void loadEmployees() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM employee";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // Clear existing data

            while (resultSet.next()) {
                int id = resultSet.getInt("idEmploye");
                String name = resultSet.getString("nom");
                String firstName = resultSet.getString("prenom");
                int age = resultSet.getInt("age");
                String phone = resultSet.getString("numeroTelephone");
                String position = resultSet.getString("poste");
                int agencyId = resultSet.getInt("idAgence");
                String cin = resultSet.getString("cin");
                tableModel.addRow(new Object[]{id, name, firstName, age, phone, position, agencyId, cin});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Ajouter Employé", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panneau du formulaire
        JPanel formPanel = new JPanel(new GridLayout(8, 2));
        formPanel.setBackground(Color.WHITE);

        JTextField nameField = new JTextField(10);
        JTextField firstNameField = new JTextField(10);
        JTextField ageField = new JTextField(3);
        JTextField phoneField = new JTextField(10);
        JTextField positionField = new JTextField(10);
        JComboBox<String> agencyComboBox = new JComboBox<>();
        JTextField cinField = new JTextField(10);

        // Remplir la liste déroulante des agences
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idAgence, nomAgence FROM agences"; // Table correcte et colonnes
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                agencyComboBox.addItem(resultSet.getString("nomAgence"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Erreur de chargement des agences: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        formPanel.add(new JLabel("Nom:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Prénom:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Âge:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Téléphone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Poste:"));
        formPanel.add(positionField);
        formPanel.add(new JLabel("ID Agence:"));
        formPanel.add(agencyComboBox);
        formPanel.add(new JLabel("CIN:"));
        formPanel.add(cinField);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Sauvegarder");
        JButton cancelButton = new JButton("Annuler");

        // Style des boutons
        JButton[] dialogButtons = {saveButton, cancelButton};
        for (JButton button : dialogButtons) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBackground(new Color(189, 157, 126)); // #b5927f
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(189, 157, 126), 1));
            button.setOpaque(true);
            button.setFocusPainted(false);
        }

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveEmployee(nameField.getText(), firstNameField.getText(),
                ageField.getText(), phoneField.getText(), positionField.getText(),
                agencyComboBox.getSelectedItem().toString(), cinField.getText(), dialog));

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void saveEmployee(String name, String firstName, String ageStr, String phone, String position,
                              String agency, String cin, JDialog dialog) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO employee (nom, prenom, age, numeroTelephone, poste, idAgence, cin) " +
                    "VALUES (?, ?, ?, ?, ?, (SELECT idAgence FROM agences WHERE nomAgence = ?), ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, firstName);
            statement.setInt(3, Integer.parseInt(ageStr));
            statement.setString(4, phone);
            statement.setString(5, position);
            statement.setString(6, agency);
            statement.setString(7, cin);
            statement.executeUpdate();

            statement.close();
            dialog.dispose();
            loadEmployees(); // Reload the employee data
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de sauvegarde: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) employeeTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet employé ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String query = "DELETE FROM employee WHERE idEmploye = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, id);
                    statement.executeUpdate();

                    statement.close();
                    loadEmployees(); // Reload the employee data
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Erreur de suppression: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un employé à supprimer.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchEmployee() {
        String searchTerm = searchField.getText();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM employee WHERE nom LIKE ? OR prenom LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // Clear existing data

            while (resultSet.next()) {
                int id = resultSet.getInt("idEmploye");
                String name = resultSet.getString("nom");
                String firstName = resultSet.getString("prenom");
                int age = resultSet.getInt("age");
                String phone = resultSet.getString("numeroTelephone");
                String position = resultSet.getString("poste");
                int agencyId = resultSet.getInt("idAgence");
                String cin = resultSet.getString("cin");
                tableModel.addRow(new Object[]{id, name, firstName, age, phone, position, agencyId, cin});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de recherche: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goToHomePage() {
        // Logic to navigate to the home page
        dispose(); // Close the current frame
        // Open the home page here
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeManagement().setVisible(true));
    }
}
