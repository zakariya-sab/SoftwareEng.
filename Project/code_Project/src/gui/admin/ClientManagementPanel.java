package gui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import services.AdminService;
import models.Client;
import models.OperationResult;


public class ClientManagementPanel extends JPanel {

    private AdminService adminService;

    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // constructor
    public ClientManagementPanel(AdminService adminService) {
        this.adminService = adminService;

        setupPanel();
        createComponents();
        loadClients();
    }

    // setup panel
    private void setupPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
    }

    // create component
    private void createComponents() {
        // header
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Client Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 35));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setOpaque(true);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton showAllButton = new JButton("Show All");
        showAllButton.setFont(new Font("Arial", Font.PLAIN, 14));
        showAllButton.setBackground(new Color(149, 165, 166));
        showAllButton.setForeground(Color.WHITE);
        showAllButton.setFocusPainted(false);
        showAllButton.setBorderPainted(false);
        showAllButton.setOpaque(true);
        showAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // table
        String[] columns = {"ID", "Account No.", "First Name", "Last Name", "Email", "Phone", "Balance"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientTable = new JTable(tableModel);
        clientTable.setFont(new Font("Arial", Font.PLAIN, 13));
        clientTable.setRowHeight(35);
        clientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        clientTable.getTableHeader().setBackground(new Color(44, 62, 80));
        clientTable.getTableHeader().setForeground(Color.WHITE);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setSelectionBackground(new Color(52, 152, 219));
        clientTable.setSelectionForeground(Color.WHITE);
        clientTable.setGridColor(new Color(220, 220, 220));

        // Set column widths
        clientTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        clientTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Account
        clientTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // First Name
        clientTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Last Name
        clientTable.getColumnModel().getColumn(4).setPreferredWidth(150);  // Email
        clientTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Phone
        clientTable.getColumnModel().getColumn(6).setPreferredWidth(100);  // Balance

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // button panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton addButton = createActionButton("Add Client", new Color(46, 204, 113));
        JButton editButton = createActionButton("Edit Client", new Color(52, 152, 219));
        JButton deleteButton = createActionButton("Delete Client", new Color(231, 76, 60));
        JButton refreshButton = createActionButton("Refresh", new Color(149, 165, 166));

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);

        // event listeners
        searchButton.addActionListener(e -> searchClients());
        showAllButton.addActionListener(e -> loadClients());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchClients();
                }
            }
        });

        addButton.addActionListener(e -> showAddClientDialog());
        editButton.addActionListener(e -> showEditClientDialog());
        deleteButton.addActionListener(e -> deleteSelectedClient());
        refreshButton.addActionListener(e -> loadClients());

        // add to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    // create action button
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // load clients
    private void loadClients() {
        tableModel.setRowCount(0);

        try {
            List<Client> clients = adminService.getAllClients();

            for (Client c : clients) {
                tableModel.addRow(new Object[]{
                        c.getUserId(),
                        c.getAccountNumber(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getPhoneNumber(),
                        String.format("%.2f", c.getBalance())
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading clients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // search clients
    private void searchClients() {
        String searchTerm = searchField.getText().trim();

        if (searchTerm.isEmpty()) {
            loadClients();
            return;
        }

        tableModel.setRowCount(0);

        try {
            List<Client> clients = adminService.findClient(searchTerm);

            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No clients found matching: " + searchTerm,
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Client c : clients) {
                tableModel.addRow(new Object[]{
                        c.getUserId(),
                        c.getAccountNumber(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getPhoneNumber(),
                        String.format("%.2f", c.getBalance())
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // show add client dialog
    private void showAddClientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add New Client", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Fields
        JTextField firstNameField = createDialogField(panel, "First Name:");
        JTextField lastNameField = createDialogField(panel, "Last Name:");
        JTextField emailField = createDialogField(panel, "Email:");
        JTextField phoneField = createDialogField(panel, "Phone:");
        JTextField accountField = createDialogField(panel, "Account Number:");
        JPasswordField passwordField = createDialogPasswordField(panel, "Password:");
        JTextField balanceField = createDialogField(panel, "Initial Balance:");
        balanceField.setText("0.00");

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonPanel);

        // Actions
        saveButton.addActionListener(e -> {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String account = accountField.getText().trim();
                String password = new String(passwordField.getPassword());
                double balance = Double.parseDouble(balanceField.getText().trim());

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                        account.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields");
                    return;
                }

                Client newClient = new Client(0, firstName, lastName, email,
                        phone, password, account, balance);

                OperationResult result = adminService.addClient(newClient);

                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    loadClients();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid balance amount");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // show edit client log
    private void showEditClientDialog() {
        int selectedRow = clientTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a client to edit",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get selected client data
        String accountNumber = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            Client client = adminService.getClientByAccountNumber(accountNumber);

            if (client == null) {
                JOptionPane.showMessageDialog(this, "Client not found");
                return;
            }

            // Create dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Edit Client", true);
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            // Fields with existing values
            JTextField firstNameField = createDialogField(panel, "First Name:");
            firstNameField.setText(client.getFirstName());

            JTextField lastNameField = createDialogField(panel, "Last Name:");
            lastNameField.setText(client.getLastName());

            JTextField emailField = createDialogField(panel, "Email:");
            emailField.setText(client.getEmail());

            JTextField phoneField = createDialogField(panel, "Phone:");
            phoneField.setText(client.getPhoneNumber());

            JTextField balanceField = createDialogField(panel, "Balance:");
            balanceField.setText(String.format("%.2f", client.getBalance()));

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

            JButton saveButton = new JButton("Update");
            saveButton.setBackground(new Color(52, 152, 219));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            saveButton.setBorderPainted(false);
            saveButton.setOpaque(true);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(new Color(149, 165, 166));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);
            cancelButton.setBorderPainted(false);
            cancelButton.setOpaque(true);

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            panel.add(Box.createVerticalStrut(20));
            panel.add(buttonPanel);

            // Actions
            saveButton.addActionListener(e -> {
                try {
                    client.setFirstName(firstNameField.getText().trim());
                    client.setLastName(lastNameField.getText().trim());
                    client.setEmail(emailField.getText().trim());
                    client.setPhoneNumber(phoneField.getText().trim());
                    client.setBalance(Double.parseDouble(balanceField.getText().trim()));

                    OperationResult result = adminService.updateClient(client);

                    if (result.isSuccess()) {
                        JOptionPane.showMessageDialog(dialog, result.getMessage());
                        dialog.dispose();
                        loadClients();
                    } else {
                        JOptionPane.showMessageDialog(dialog, result.getMessage());
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.add(panel);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // delete selected client
    private void deleteSelectedClient() {
        int selectedRow = clientTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a client to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String accountNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String clientName = tableModel.getValueAt(selectedRow, 2) + " " +
                tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete client:\n" + clientName +
                        "\n(Account: " + accountNumber + ")\n\nThis action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                OperationResult result = adminService.deleteClient(accountNumber);

                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, result.getMessage());
                    loadClients();
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage());
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // helper:create dialog field:
    private JTextField createDialogField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        parent.add(field);
        parent.add(Box.createVerticalStrut(10));

        return field;
    }

    // helper :create dialog password field
    private JPasswordField createDialogPasswordField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        parent.add(field);
        parent.add(Box.createVerticalStrut(10));

        return field;
    }
}