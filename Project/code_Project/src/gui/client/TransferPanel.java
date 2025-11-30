package gui.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import services.ClientService;
import models.Client;
import models.OperationResult;
import gui.ClientDashboard;


public class TransferPanel extends JPanel {

    private Client client;
    private ClientService clientService;
    private ClientDashboard dashboard;

    private JTextField recipientField;
    private JTextField amountField;
    private JLabel balanceLabel;
    private JLabel messageLabel;

    // constructor
    public TransferPanel(Client client, ClientService clientService, ClientDashboard dashboard) {
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
        // Title
        JLabel titleLabel = new JLabel("Transfer Money");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        formPanel.setMaximumSize(new Dimension(500, 500));

        // Balance Display
        JPanel balancePanel = createBalanceDisplay();

        // From Account (read-only)
        JLabel fromLabel = new JLabel("From Account:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fromLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField fromField = new JTextField(client.getAccountNumber());
        fromField.setFont(new Font("Arial", Font.PLAIN, 16));
        fromField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fromField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fromField.setEditable(false);  // Cannot change
        fromField.setBackground(new Color(240, 240, 240));

        // To Account
        JLabel toLabel = new JLabel("To Account Number:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        recipientField = new JTextField();
        recipientField.setFont(new Font("Arial", Font.PLAIN, 16));
        recipientField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        recipientField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Amount
        JLabel amountLabel = new JLabel("Amount (MAD):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Transfer Button (blue color)
        JButton transferButton = new JButton("Transfer");
        transferButton.setFont(new Font("Arial", Font.BOLD, 16));
        transferButton.setBackground(new Color(52, 152, 219));  // Blue
        transferButton.setForeground(Color.WHITE);
        transferButton.setFocusPainted(false);
        transferButton.setBorderPainted(false);
        transferButton.setOpaque(true);
        transferButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        transferButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        transferButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to form
        formPanel.add(balancePanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(fromLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(fromField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(toLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(recipientField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(amountField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(transferButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(messageLabel);

        // Center wrapper
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(formPanel);

        // Action listener
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTransfer();
            }
        });

        // Enter key on amount field
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performTransfer();
                }
            }
        });

        add(titleLabel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    // ============== CREATE BALANCE DISPLAY ==============
    private JPanel createBalanceDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(52, 152, 219));  // Blue
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel("Available Balance");
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

    // perform transfer
    private void performTransfer() {
        String recipient = recipientField.getText().trim();
        String amountText = amountField.getText().trim();

        // Validate recipient
        if (recipient.isEmpty()) {
            showMessage("Please enter recipient account number", false);
            return;
        }

        // Cannot transfer to yourself
        if (recipient.equals(client.getAccountNumber())) {
            showMessage("Cannot transfer to your own account", false);
            return;
        }

        // Validate amount
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

        // Check balance
        double currentBalance = getLatestBalance();
        if (amount > currentBalance) {
            showMessage("Insufficient balance! Available: " +
                    String.format("%.2f MAD", currentBalance), false);
            return;
        }

        // Confirm transfer
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Transfer " + String.format("%.2f MAD", amount) + " to account " + recipient + "?",
                "Confirm Transfer",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Perform transfer
        try {
            OperationResult result = clientService.transfer(
                    client.getAccountNumber(),
                    recipient,
                    amount
            );

            if (result.isSuccess()) {
                showMessage(result.getMessage(), true);
                recipientField.setText("");
                amountField.setText("");
                updateBalance();
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