package com.abb.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import com.abb.db.DatabaseConnection;

public class HomePage extends JFrame {

    private JPanel sidebarPanel;
    private JPanel mainPanel;
    private JLabel userLabel;
    private JButton toggleSidebarButton;
    private boolean isSidebarVisible = false;
    private JLabel imageLabel;

    public HomePage(String username) {
        super("Home Page - Al Barid Bank");

        // Configuration principale de la fenêtre
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation de la barre latérale
        initializeSidebar(username);

        // Panneau principal
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255)); // Couleur de fond du panneau principal

        JLabel welcomeLabel = new JLabel("Bienvenue sur la page d'accueil", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(133, 116, 110)); // Couleur plus moderne

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Ajouter un panneau pour l'image en haut à droite
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setOpaque(true);
        imagePanel.setBackground(new Color(133, 116, 110)); // Couleur de fond du panneau d'image
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espacement autour de l'image

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Centrer l'image dans le panneau
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement autour de l'image

        imageLabel = new JLabel();
        try {
            URL imageUrl = new URL("https://imagizer.imageshack.com/img922/1719/PH8P75.png"); // Remplacez par votre URL
            ImageIcon imageIcon = new ImageIcon(imageUrl);
            Image image = imageIcon.getImage();
            
            // Redimensionner l'image tout en conservant ses proportions
            int desiredWidth = 150; // Nouvelle largeur souhaitée
            int desiredHeight = 100; // Nouvelle hauteur souhaitée
            
            double widthRatio = (double) desiredWidth / image.getWidth(null);
            double heightRatio = (double) desiredHeight / image.getHeight(null);
            double scaleRatio = Math.min(widthRatio, heightRatio);
            
            int newWidth = (int) (image.getWidth(null) * scaleRatio);
            int newHeight = (int) (image.getHeight(null) * scaleRatio);
            
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(imageIcon);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        imagePanel.add(imageLabel, gbc);

        // Ajouter le panneau d'image en haut à droite
        mainPanel.add(imagePanel, BorderLayout.NORTH);

        // Créer et ajouter un graphique
        JPanel chartPanel = createChartPanel();
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Ajout d'un bouton pour basculer la visibilité de la barre latérale
        toggleSidebarButton = new JButton("|||") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicButtonUI() {
                    @Override
                    public void paint(Graphics g, JComponent c) {
                        JButton button = (JButton) c;
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(button.getBackground());
                        g2d.fill(new RoundRectangle2D.Double(0, 0, button.getWidth(), button.getHeight(), 20, 20));
                        super.paint(g, c);
                    }
                });
            }
        };
        toggleSidebarButton.setPreferredSize(new Dimension(50, 50));
        toggleSidebarButton.setFont(new Font("Arial", Font.BOLD, 24));
        toggleSidebarButton.setBackground(new Color(255, 206, 35)); // Couleur de fond du bouton
        toggleSidebarButton.setForeground(new Color(133, 116, 110)); // Couleur du texte du bouton
        toggleSidebarButton.setBorder(BorderFactory.createEmptyBorder());
        toggleSidebarButton.setFocusPainted(false);
        toggleSidebarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSidebar();
            }
        });

        mainPanel.add(toggleSidebarButton, BorderLayout.WEST);
    }

    private void initializeSidebar(String username) {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(new Color(133, 116, 110)); // Couleur de fond moderne
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.setOpaque(true);

        userLabel = new JLabel("Bonjour " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Ajouter de l'espace au-dessus et au-dessous

        JButton gestionAgencesButton = new JButton("Gestion d'agences") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicButtonUI() {
                    @Override
                    public void paint(Graphics g, JComponent c) {
                        JButton button = (JButton) c;
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(button.getBackground());
                        g2d.fill(new RoundRectangle2D.Double(0, 0, button.getWidth(), button.getHeight(), 20, 20));
                        super.paint(g, c);
                    }
                });
            }
        };
        gestionAgencesButton.setFont(new Font("Arial", Font.BOLD, 14));
        gestionAgencesButton.setForeground(Color.WHITE);
        gestionAgencesButton.setBackground(new Color(255, 206, 35)); // Couleur de fond du bouton
        gestionAgencesButton.setFocusPainted(false);
        gestionAgencesButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        gestionAgencesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gestionAgencesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                gestionAgencesButton.setBackground(new Color(181, 147, 127)); // Couleur de survol
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                gestionAgencesButton.setBackground(new Color(255, 206, 35)); // Couleur d'origine
            }
        });
        gestionAgencesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAgencyManagement();
            }
        });

        JButton gestionEmployesButton = new JButton("Gestion d'employés") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicButtonUI() {
                    @Override
                    public void paint(Graphics g, JComponent c) {
                        JButton button = (JButton) c;
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(button.getBackground());
                        g2d.fill(new RoundRectangle2D.Double(0, 0, button.getWidth(), button.getHeight(), 20, 20));
                        super.paint(g, c);
                    }
                });
            }
        };
        gestionEmployesButton.setFont(new Font("Arial", Font.BOLD, 14));
        gestionEmployesButton.setForeground(Color.WHITE);
        gestionEmployesButton.setBackground(new Color(255, 206, 35)); // Couleur de fond du bouton
        gestionEmployesButton.setFocusPainted(false);
        gestionEmployesButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        gestionEmployesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gestionEmployesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                gestionEmployesButton.setBackground(new Color(181, 147, 127)); // Couleur de survol
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                gestionEmployesButton.setBackground(new Color(255, 206, 35)); // Couleur d'origine
            }
        });
        gestionEmployesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEmployeeManagement();
            }
        });

        JButton logoutButton = new JButton("Déconnexion") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new BasicButtonUI() {
                    @Override
                    public void paint(Graphics g, JComponent c) {
                        JButton button = (JButton) c;
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(button.getBackground());
                        g2d.fill(new RoundRectangle2D.Double(0, 0, button.getWidth(), button.getHeight(), 20, 20));
                        super.paint(g, c);
                    }
                });
            }
        };
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(255, 206, 35)); // Couleur de fond du bouton
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(181, 147, 127)); // Couleur de survol
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(255, 206, 35)); // Couleur d'origine
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    logout();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(userLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(gestionAgencesButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(gestionEmployesButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.setVisible(isSidebarVisible); 
    }

    private JPanel createChartPanel() {
        String chartTitle = "Distribution des agences par région";

        DefaultPieDataset dataset = createDataset();
        JFreeChart chart = ChartFactory.createPieChart(chartTitle, dataset, true, true, false);

        return new ChartPanel(chart);
    }

    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try {
            Connection connection = DatabaseConnection.getConnection();

            if (connection != null) {
                Statement statement = connection.createStatement();
                // Joindre la table agences avec la table regions pour obtenir les noms des régions
                String query = "SELECT r.libelleRegion, COUNT(*) AS nombre_agences " +
                               "FROM agences a " +
                               "JOIN region r ON a.idRegion = r.idRegion " +
                               "GROUP BY r.libelleRegion";
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    String nomRegion = resultSet.getString("libelleRegion");
                    int nombreAgences = resultSet.getInt("nombre_agences");
                    dataset.setValue(nomRegion, nombreAgences);
                }

                resultSet.close();
                statement.close();
            } else {
                System.out.println("La connexion à la base de données n'est pas établie.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private void toggleSidebar() {
        if (isSidebarVisible) {
            sidebarPanel.setVisible(false);
        } else {
            sidebarPanel.setVisible(true);
        }
        isSidebarVisible = !isSidebarVisible;
    }

    private void openAgencyManagement() {
        SwingUtilities.invokeLater(() -> {
            AgencyManagement agencyManagement = new AgencyManagement();
            agencyManagement.setVisible(true);
        });
    }

    private void openEmployeeManagement() {
        SwingUtilities.invokeLater(() -> {
            EmployeeManagement employeeManagement = new EmployeeManagement();
            employeeManagement.setVisible(true);
        });
    }

    private void logout() throws MalformedURLException {
        // Code pour la déconnexion
        System.out.println("Déconnexion");
        dispose();
        new Login().setVisible(true);
    }

    public static void main(String[] args) {
        if (DatabaseConnection.connect()) {
            SwingUtilities.invokeLater(() -> {
                HomePage dashboard = new HomePage("NomUtilisateur");
                dashboard.setVisible(true);
            });
        } else {
            System.out.println("Échec de la connexion à la base de données.");
        }
    }
}
