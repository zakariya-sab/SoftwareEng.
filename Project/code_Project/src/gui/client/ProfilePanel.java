package gui.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import services.ClientService;
import services.AuthenticationService;
import models.Client;
import models.OperationResult;


public class ProfilePanel extends JPanel {

    private Client client;
    private ClientService clientService;
    private AuthenticationService authService;

    // Profile fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;

    // Password fields
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private JLabel messageLabel;
    private JLabel passwordMessageLabel;

    // constructor
    public ProfilePanel(Client client, ClientService clientService, AuthenticationService authService) {
        this.client = client;
        this.clientService = clientService;
        this.authService = authService;

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
        JLabel titleLabel = new JLabel("Profile Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Main content panel with two cards side by side
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Left: Personal Info
        contentPanel.add(createPersonalInfoCard());

        // Right: Change Password
        contentPanel.add(createPasswordCard());

        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    // create personal info card
    private JPanel createPersonalInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        // Card title
        JLabel cardTitle = new JLabel("Personal Information");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 18));
        cardTitle.setForeground(new Color(44, 62, 80));
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // First Name
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        firstNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        firstNameField = new JTextField(client.getFirstName());
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        firstNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Last Name
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        lastNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lastNameField = new JTextField(client.getLastName());
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        lastNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField(client.getEmail());
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Phone
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        phoneField = new JTextField(client.getPhoneNumber());
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Account Number (read-only)
        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField accountField = new JTextField(client.getAccountNumber());
        accountField.setFont(new Font("Arial", Font.PLAIN, 14));
        accountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        accountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountField.setEditable(false);
        accountField.setBackground(new Color(240, 240, 240));

        // Update button
        JButton updateButton = new JButton("Update Information");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setOpaque(true);
        updateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Message
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to card
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(20));
        card.add(firstNameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(firstNameField);
        card.add(Box.createVerticalStrut(12));
        card.add(lastNameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(lastNameField);
        card.add(Box.createVerticalStrut(12));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(phoneLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(phoneField);
        card.add(Box.createVerticalStrut(12));
        card.add(accountLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(accountField);
        card.add(Box.createVerticalStrut(20));
        card.add(updateButton);
        card.add(Box.createVerticalStrut(10));
        card.add(messageLabel);

        // Action listener
        updateButton.addActionListener(e -> updatePersonalInfo());

        return card;
    }

    // create password card
    private JPanel createPasswordCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        // Card title
        JLabel cardTitle = new JLabel("Change Password");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 18));
        cardTitle.setForeground(new Color(44, 62, 80));
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Current Password
        JLabel currentLabel = new JLabel("Current Password:");
        currentLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        currentPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        currentPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // New Password
        JLabel newLabel = new JLabel("New Password:");
        newLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        newLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm New Password:");
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Change button
        JButton changeButton = new JButton("Change Password");
        changeButton.setFont(new Font("Arial", Font.BOLD, 14));
        changeButton.setBackground(new Color(230, 126, 34));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFocusPainted(false);
        changeButton.setBorderPainted(false);
        changeButton.setOpaque(true);
        changeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        changeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Message
        passwordMessageLabel = new JLabel(" ");
        passwordMessageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordMessageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to card
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(20));
        card.add(currentLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(currentPasswordField);
        card.add(Box.createVerticalStrut(15));
        card.add(newLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(newPasswordField);
        card.add(Box.createVerticalStrut(15));
        card.add(confirmLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(25));
        card.add(changeButton);
        card.add(Box.createVerticalStrut(10));
        card.add(passwordMessageLabel);
        card.add(Box.createVerticalGlue());  // Push everything up

        // Action listener
        changeButton.addActionListener(e -> changePassword());

        return card;
    }

    // update personal info
    private void updatePersonalInfo() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showMessage(messageLabel, "First name, last name and email are required", false);
            return;
        }

        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            showMessage(messageLabel, "Please enter a valid email", false);
            return;
        }

        try {
            // Update client object
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setEmail(email);
            client.setPhoneNumber(phone);

            // Save to database
            OperationResult result = clientService.updateClientInfo(client);

            if (result.isSuccess()) {
                showMessage(messageLabel, "Information updated successfully!", true);
            } else {
                showMessage(messageLabel, result.getMessage(), false);
            }

        } catch (Exception e) {
            showMessage(messageLabel, "Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // change password
    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage(passwordMessageLabel, "All password fields are required", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage(passwordMessageLabel, "New passwords do not match", false);
            return;
        }

        if (newPassword.length() < 4) {
            showMessage(passwordMessageLabel, "Password must be at least 4 characters", false);
            return;
        }

        try {
            OperationResult result = authService.changePassword(currentPassword, newPassword);

            if (result.isSuccess()) {
                showMessage(passwordMessageLabel, "Password changed successfully!", true);
                // Clear fields
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } else {
                showMessage(passwordMessageLabel, result.getMessage(), false);
            }

        } catch (Exception e) {
            showMessage(passwordMessageLabel, "Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // show message
    private void showMessage(JLabel label, String message, boolean success) {
        label.setText(message);
        label.setForeground(success ? new Color(46, 204, 113) : new Color(231, 76, 60));
    }
}