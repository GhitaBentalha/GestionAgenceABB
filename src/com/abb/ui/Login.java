package com.abb.ui;

import com.abb.db.DatabaseConnection;
import com.abb.models.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    private JPasswordField passTextField;
    private JButton togglePasswordButton;
    private JLabel errorMessageLabel;

    public Login() throws MalformedURLException {
        super("Login - Al Barid Bank");

        if (DatabaseConnection.connect()) {
            Admin.createAdminIfNotExists();
        } else {
            JOptionPane.showMessageDialog(this, "Échec de la connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
    }

    private void initComponents() throws MalformedURLException {
        // Configuration principale de la fenêtre
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Utiliser un panneau principal avec un gestionnaire de dispositions BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(152, 123, 106)); // Couleur de fond

        // Charger l'image à partir d'une URL et redimensionner
        try {
            URL imageUrl = new URL("https://i.ibb.co/VLDDNcD/image-2024-09-12-140733951.png");
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(100, 80, Image.SCALE_SMOOTH);
            ImageIcon logoIcon = new ImageIcon(scaledImg);
            JLabel logoLabel = new JLabel("AL BARID BANK", logoIcon, JLabel.CENTER);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
            logoLabel.setHorizontalTextPosition(JLabel.CENTER);
            logoLabel.setForeground(Color.WHITE);

            JPanel logoPanel = new JPanel();
            logoPanel.setBackground(new Color(152, 123, 106)); // S'assurer que le fond est assorti
            logoPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0)); // Ajouter de l'espace avant l'image
            logoPanel.add(logoLabel);

            mainPanel.add(logoPanel, BorderLayout.NORTH);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        // Panneau central pour les champs de texte
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(152, 123, 106)); // Correspond à la couleur de fond

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Réduire les marges internes
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Permettra de mettre les composants chacun sur une nouvelle ligne

        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setForeground(Color.WHITE);
        JTextField userTextField = roundedTextField(24); // Réduire la taille des champs

        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setForeground(Color.WHITE);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passTextField = roundedPasswordField(20); // Réduire la taille des champs

        togglePasswordButton = new JButton("👁"); // Icône ou texte pour le bouton
        togglePasswordButton.setPreferredSize(new Dimension(40, 35)); // Ajuster la taille du bouton
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setBorder(BorderFactory.createEmptyBorder());
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });

        passwordPanel.add(passTextField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(Color.RED); // Texte rouge pour le message d'erreur
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        centerPanel.add(userLabel, gbc);
        centerPanel.add(userTextField, gbc);
        centerPanel.add(passLabel, gbc);
        centerPanel.add(passwordPanel, gbc);
        centerPanel.add(errorMessageLabel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Ajouter le bouton en bas
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(152, 123, 106)); // S'assurer que le fond est assorti
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Ajouter de l'espace après le bouton
        JButton loginButton = roundedButton("SE CONNECTER", new Color(253, 208, 23), Color.WHITE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCredentials(userTextField.getText(), new String(passTextField.getPassword()));
            }
        });

        bottomPanel.add(loginButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void togglePasswordVisibility() {
        if (passTextField.getEchoChar() == '\u2022') { // Si le mot de passe est masqué
            passTextField.setEchoChar((char) 0); // Afficher le mot de passe
        } else {
            passTextField.setEchoChar('\u2022'); // Masquer le mot de passe
        }
    }

    private void checkCredentials(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Le nom d'utilisateur ou le mot de passe ne peut pas être vide.");
            return;
        }

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM Admin WHERE username=? AND password=?");
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Connecté avec succès
                JOptionPane.showMessageDialog(this, "Connecté avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                // Ouvrir la page d'accueil
                SwingUtilities.invokeLater(() -> {
                    new HomePage(password).setVisible(true);
                    dispose(); // Ferme la fenêtre de connexion actuelle
                });
                
            } else {
                errorMessageLabel.setText("Nom d'utilisateur et/ou mot de passe est incorrect");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Erreur de connexion à la base de données.");
        }
    }


    private JTextField roundedTextField(int columns) {
        JTextField textField = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    RoundedBorder border = (RoundedBorder) getBorder();
                    g.setColor(getBackground());
                    g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, border.getRadius(), border.getRadius());
                }
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(new RoundedBorder(15)); // Ajuster le rayon selon votre besoin
            }
        };
        textField.setPreferredSize(new Dimension(100, 45)); // Réduire la taille
        return textField;
    }

    private JPasswordField roundedPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    RoundedBorder border = (RoundedBorder) getBorder();
                    g.setColor(getBackground());
                    g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, border.getRadius(), border.getRadius());
                }
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(new RoundedBorder(15)); // Utilisation de la bordure arrondie
                setMargin(new Insets(5, 10, 5, 10)); // Ajout de marges internes pour éviter le texte coupé
            }
        };
        passwordField.setPreferredSize(new Dimension(100, 45)); // Taille ajustée pour plus d'espace pour le texte
        passwordField.setEchoChar('\u2022'); // Masquer le mot de passe par défaut
        return passwordField;
    }

    private JButton roundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    RoundedBorder border = (RoundedBorder) getBorder();
                    g.setColor(getBackground());
                    g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, border.getRadius(), border.getRadius());
                }
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(new RoundedBorder(20)); // Ajuster le rayon selon votre besoin
                setBackground(bgColor);
                setForeground(fgColor);
                setPreferredSize(new Dimension(200, 40)); // Ajuster la taille selon votre besoin
                setFont(getFont().deriveFont(Font.BOLD));
            }
        };
        return button;
    }

    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        public int getRadius() {
            return radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getBackground());
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius + 1;
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
			try {
				new Login().setVisible(true);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
    }
}