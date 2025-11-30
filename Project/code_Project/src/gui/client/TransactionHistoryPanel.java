package gui.client;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import services.ClientService;
import models.Client;
import models.Transaction;


public class TransactionHistoryPanel extends JPanel {

    private Client client;
    private ClientService clientService;

    private JTable transactionTable;
    private DefaultTableModel tableModel;

    // constructor
    public TransactionHistoryPanel(Client client, ClientService clientService) {
        this.client = client;
        this.clientService = clientService;

        setupPanel();
        createComponents();
        loadTransactions();
    }

    // setup panel
    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(236, 240, 241));
    }

    // create components
    private void createComponents() {
        // title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTransactions());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        // table
        // Column names
        String[] columns = {"Date", "Type", "From", "To", "Amount", "Description"};

        // Create table model (this holds the data)
        tableModel = new DefaultTableModel(columns, 0) {
            // Make table read-only
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create the table
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 13));
        transactionTable.setRowHeight(35);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        transactionTable.getTableHeader().setBackground(new Color(52, 73, 94));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.setSelectionBackground(new Color(52, 152, 219));
        transactionTable.setSelectionForeground(Color.WHITE);
        transactionTable.setGridColor(new Color(220, 220, 220));

        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(150);  // Date
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Type
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // From
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // To
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Amount
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(200);  // Description

        // Custom renderer for Type column (color based on type)
        transactionTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String type = (String) value;
                    if ("DEPOSIT".equals(type)) {
                        c.setForeground(new Color(46, 204, 113));  // Green
                    } else if ("WITHDRAWAL".equals(type)) {
                        c.setForeground(new Color(231, 76, 60));   // Red
                    } else if ("TRANSFER".equals(type)) {
                        c.setForeground(new Color(52, 152, 219));  // Blue
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Custom renderer for Amount column
        transactionTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });

        // Scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // add to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // load transactions
    private void loadTransactions() {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            // Get transactions from database
            List<Transaction> transactions = clientService.getTransactionHistory(
                    client.getAccountNumber()
            );

            if (transactions == null || transactions.isEmpty()) {
                // Show message if no transactions
                tableModel.addRow(new Object[]{
                        "", "No transactions found", "", "", "", ""
                });
                return;
            }

            // Add each transaction as a row
            for (Transaction t : transactions) {
                // Format the date nicely
                String dateStr = t.getTimestamp().toString().replace("T", " ");
                if (dateStr.length() > 16) {
                    dateStr = dateStr.substring(0, 16);  // Remove seconds
                }

                // Format amount
                String amountStr = String.format("%.2f MAD", t.getAmount());

                // Determine from/to display
                String from = t.getFromAccountNumber();
                String to = t.getToAccountNumber();

                // If this is my account, highlight it
                if (client.getAccountNumber().equals(from)) {
                    from = from + " (You)";
                }
                if (client.getAccountNumber().equals(to)) {
                    to = to + " (You)";
                }

                // Handle null values
                if (from == null) from = "-";
                if (to == null) to = "-";

                tableModel.addRow(new Object[]{
                        dateStr,
                        t.getTransactionType(),
                        from,
                        to,
                        amountStr,
                        t.getDescription()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading transactions: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}