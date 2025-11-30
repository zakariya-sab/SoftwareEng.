package gui.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import services.ClientService;
import models.Client;
import models.OperationResult;
import gui.ClientDashboard;


public class DepositPanel extends JPanel {

    private Client client;
    private ClientService clientService;
    private ClientDashboard dashboard;  // Reference to parent for refresh

    // Components
    private JTextField amountField;
    private JLabel balanceLabel;
    private JLabel messageLabel;

    // constructor
    public DepositPanel(Client client, ClientService clientService, ClientDashboard dashboard) {
        this.client = client;
        this.clientService = clientService;
        this.dashboard = dashboard;

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
        JLabel titleLabel = new JLabel("Deposit Money");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        formPanel.setMaximumSize(new Dimension(500, 400));

        // Current Balance Display
        JPanel balancePanel = createBalanceDisplay();

        // Amount Input
        JLabel amountLabel = new JLabel("Amount to Deposit (MAD):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 18));
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Deposit Button
        JButton depositButton = new JButton("Deposit");
        depositButton.setFont(new Font("Arial", Font.BOLD, 16));
        depositButton.setBackground(new Color(46, 204, 113));
        depositButton.setForeground(Color.WHITE);
        depositButton.setFocusPainted(false);
        depositButton.setBorderPainted(false);
        depositButton.setOpaque(true);
        depositButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        depositButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        depositButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Message Label (for success/error)
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to form
        formPanel.add(balancePanel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(amountField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(depositButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(messageLabel);

        // Center the form
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(formPanel);

        // Add action listener
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDeposit();
            }
        });

        // Enter key support
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performDeposit();
                }
            }
        });

        add(titleLabel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    // create balance display
    private JPanel createBalanceDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(46, 204, 113));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel("Current Balance");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        balanceLabel = new JLabel(String.format("%.2f MAD", getLatestBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(balanceLabel);

        return panel;
    }

    // perform deposit
    private void performDeposit() {
        String amountText = amountField.getText().trim();

        // Validate input
        if (amountText.isEmpty()) {
            showMessage("Please enter an amount", false);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number", false);
            return;
        }

        if (amount <= 0) {
            showMessage("Amount must be greater than 0", false);
            return;
        }

        // Perform deposit
        try {
            OperationResult result = clientService.deposit(client.getAccountNumber(), amount);

            if (result.isSuccess()) {
                showMessage(result.getMessage(), true);
                amountField.setText("");

                // Refresh balance display
                updateBalance();

                // Refresh dashboard data
                dashboard.refreshClientData();

            } else {
                showMessage(result.getMessage(), false);
            }

        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // show message
    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? new Color(46, 204, 113) : new Color(231, 76, 60));
    }

    // update balance
    private void updateBalance() {
        balanceLabel.setText(String.format("%.2f MAD", getLatestBalance()));
    }

    // get latest balance
    private double getLatestBalance() {
        try {
            double balance = clientService.getBalance(client.getAccountNumber());
            if (balance >= 0) {
                return balance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client.getBalance();
    }
}