package gui.client;

import javax.swing.*;
import java.awt.*;
import services.ClientService;
import models.Client;


public class ClientHomePanel extends JPanel {

    private Client client;
    private ClientService clientService;

    // construcor
    public ClientHomePanel(Client client, ClientService clientService) {
        this.client = client;
        this.clientService = clientService;

        setupPanel();
        createComponents();
    }

    // setup panel
    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(236, 240, 241));
    }

    // create components
    private void createComponents() {
        // title
        JLabel titleLabel = new JLabel("Account Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // cards panel
        // Contains the info cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);

        // Card 1: Balance
        cardsPanel.add(createCard(
                "Current Balance",
                String.format("%.2f MAD", getLatestBalance()),
                new Color(46, 204, 113)  // Green
        ));

        // Card 2: Account Number
        cardsPanel.add(createCard(
                "Account Number",
                client.getAccountNumber(),
                new Color(52, 152, 219)  // Blue
        ));

        // Card 3: Account Holder
        cardsPanel.add(createCard(
                "Account Holder",
                client.getFirstName() + " " + client.getLastName(),
                new Color(155, 89, 182)  // Purple
        ));

        // info panel
        JPanel infoPanel = createInfoPanel();

        // add to main panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(cardsPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    // create card
    // Helper method to create info cards
    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Color bar at top
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        colorBar.setPreferredSize(new Dimension(0, 5));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(new Color(44, 62, 80));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(colorBar);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    // create info panel
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel infoTitle = new JLabel("Account Information");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 18));
        infoTitle.setForeground(new Color(44, 62, 80));

        // Info grid
        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 10, 15));
        infoGrid.setOpaque(false);
        infoGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Add info rows
        addInfoRow(infoGrid, "First Name:", client.getFirstName());
        addInfoRow(infoGrid, "Last Name:", client.getLastName());
        addInfoRow(infoGrid, "Email:", client.getEmail());
        addInfoRow(infoGrid, "Phone:", client.getPhoneNumber());

        panel.add(infoTitle, BorderLayout.NORTH);
        panel.add(infoGrid, BorderLayout.CENTER);

        return panel;
    }

    // add info row
    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        labelComponent.setForeground(new Color(127, 140, 141));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(44, 62, 80));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    // get latest balance
    // Fetch fresh balance from database
    private double getLatestBalance() {
        try {
            double balance = clientService.getBalance(client.getAccountNumber());
            if (balance >= 0) {
                return balance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client.getBalance();  // Fallback to cached value
    }
}