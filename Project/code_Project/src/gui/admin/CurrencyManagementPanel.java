package gui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import services.CurrencyService;
import models.CurrencyRate;
import models.OperationResult;


public class CurrencyManagementPanel extends JPanel {

    private CurrencyService currencyService;

    private JTable currencyTable;
    private DefaultTableModel tableModel;

    // constructor
    public CurrencyManagementPanel() {
        this.currencyService = new CurrencyService();

        setupPanel();
        createComponents();
        loadCurrencies();
    }

    // setup panel
    private void setupPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
    }

    // create components
    private void createComponents() {
        // header
        JLabel titleLabel = new JLabel("Currency Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(241, 196, 15));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel infoLabel = new JLabel("Note: All rates are relative to MAD (Moroccan Dirham). " +
                "Rate = how many MAD equals 1 unit of the currency.");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(44, 62, 80));
        infoPanel.add(infoLabel);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        // table
        String[] columns = {"Code", "Currency Name", "Rate to MAD", "Last Updated"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        currencyTable = new JTable(tableModel);
        currencyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        currencyTable.setRowHeight(40);
        currencyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        currencyTable.getTableHeader().setBackground(new Color(44, 62, 80));
        currencyTable.getTableHeader().setForeground(Color.WHITE);
        currencyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        currencyTable.setSelectionBackground(new Color(155, 89, 182));
        currencyTable.setSelectionForeground(Color.WHITE);
        currencyTable.setGridColor(new Color(220, 220, 220));

        // Column widths
        currencyTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        currencyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        currencyTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        currencyTable.getColumnModel().getColumn(3).setPreferredWidth(180);

        // Center the code column
        currencyTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 14));
            }
        });

        // Right-align rate column
        currencyTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });

        JScrollPane scrollPane = new JScrollPane(currencyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton addButton = createActionButton("Add Currency", new Color(46, 204, 113));
        JButton updateButton = createActionButton("Update Rate", new Color(52, 152, 219));
        JButton deleteButton = createActionButton("Delete Currency", new Color(231, 76, 60));
        JButton refreshButton = createActionButton("Refresh", new Color(149, 165, 166));

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);

        // event listener
        addButton.addActionListener(e -> showAddCurrencyDialog());
        updateButton.addActionListener(e -> showUpdateRateDialog());
        deleteButton.addActionListener(e -> deleteSelectedCurrency());
        refreshButton.addActionListener(e -> loadCurrencies());

        // add to panel
        add(topPanel, BorderLayout.NORTH);
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
        button.setPreferredSize(new Dimension(140, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // load currencies
    private void loadCurrencies() {
        tableModel.setRowCount(0);

        try {
            List<CurrencyRate> rates = currencyService.getAllCurrencyRates();

            for (CurrencyRate rate : rates) {
                String dateStr = rate.getLastUpdated().toString().replace("T", " ");
                if (dateStr.length() > 16) {
                    dateStr = dateStr.substring(0, 16);
                }

                tableModel.addRow(new Object[]{
                        rate.getCurrencyCode(),
                        rate.getCurrencyName(),
                        String.format("%.4f", rate.getRateToMad()),
                        dateStr
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading currencies: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // show add currency dialog
    private void showAddCurrencyDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add New Currency", true);
        dialog.setSize(350, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Fields
        JTextField codeField = createDialogField(panel, "Currency Code (e.g., USD):");
        JTextField nameField = createDialogField(panel, "Currency Name (e.g., US Dollar):");
        JTextField rateField = createDialogField(panel, "Rate to MAD:");

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("Add");
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

        panel.add(Box.createVerticalStrut(15));
        panel.add(buttonPanel);

        // Actions
        saveButton.addActionListener(e -> {
            try {
                String code = codeField.getText().trim().toUpperCase();
                String name = nameField.getText().trim();
                double rate = Double.parseDouble(rateField.getText().trim());

                if (code.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                    return;
                }

                if (code.length() != 3) {
                    JOptionPane.showMessageDialog(dialog, "Currency code must be 3 characters");
                    return;
                }

                OperationResult result = currencyService.addCurrency(code, name, rate);

                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    loadCurrencies();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid rate value");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // show update rate dialog
    private void showUpdateRateDialog() {
        int selectedRow = currencyTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a currency to update",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String currentRate = (String) tableModel.getValueAt(selectedRow, 2);

        // Can't update MAD
        if ("MAD".equals(code)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot update the base currency (MAD)",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Update Rate - " + code, true);
        dialog.setSize(350, 220);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Info label
        JLabel infoLabel = new JLabel("Currency: " + code + " - " + name);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel currentLabel = new JLabel("Current rate: " + currentRate + " MAD");
        currentLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        currentLabel.setForeground(new Color(127, 140, 141));
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(currentLabel);
        panel.add(Box.createVerticalStrut(15));

        // New rate field
        JTextField rateField = createDialogField(panel, "New Rate to MAD:");

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setOpaque(true);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        // Actions
        updateButton.addActionListener(e -> {
            try {
                double newRate = Double.parseDouble(rateField.getText().trim());

                if (newRate <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Rate must be positive");
                    return;
                }

                OperationResult result = currencyService.updateCurrencyRate(code, newRate);

                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                    dialog.dispose();
                    loadCurrencies();
                } else {
                    JOptionPane.showMessageDialog(dialog, result.getMessage());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid rate value");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // delete selected currency
    private void deleteSelectedCurrency() {
        int selectedRow = currencyTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a currency to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        // Can't delete MAD
        if ("MAD".equals(code)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete the base currency (MAD)",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete currency: " + code + " - " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                OperationResult result = currencyService.deleteCurrency(code);

                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, result.getMessage());
                    loadCurrencies();
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage());
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // helper :create dialog field
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
}