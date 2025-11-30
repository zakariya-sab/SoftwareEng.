package gui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import services.TransactionService;
import models.Transaction;


public class AllTransactionsPanel extends JPanel {

    private TransactionService transactionService;

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    // constructor
    public AllTransactionsPanel() {
        this.transactionService = new TransactionService();

        setupPanel();
        createComponents();
        loadTransactions("ALL");
    }

    // setup panel
    private void setupPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
    }

    // create components
    private void createComponents() {
        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("All Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter by Type: ");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        filterCombo = new JComboBox<>(new String[]{
                "ALL", "DEPOSIT", "WITHDRAWAL", "TRANSFER"
        });
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        filterCombo.setPreferredSize(new Dimension(150, 35));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        // table
        String[] columns = {"ID", "Date", "Type", "From Account", "To Account", "Amount", "Description"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 13));
        transactionTable.setRowHeight(35);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        transactionTable.getTableHeader().setBackground(new Color(44, 62, 80));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.setSelectionBackground(new Color(52, 152, 219));
        transactionTable.setSelectionForeground(Color.WHITE);
        transactionTable.setGridColor(new Color(220, 220, 220));

        // Column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(130);  // Date
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Type
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // From
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // To
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Amount
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(200);  // Description

        // Color the Type column
        transactionTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String type = (String) value;
                    if ("DEPOSIT".equals(type)) {
                        c.setForeground(new Color(46, 204, 113));
                    } else if ("WITHDRAWAL".equals(type)) {
                        c.setForeground(new Color(231, 76, 60));
                    } else if ("TRANSFER".equals(type)) {
                        c.setForeground(new Color(52, 152, 219));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Right-align amount column
        transactionTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // summary panel
        JPanel summaryPanel = createSummaryPanel();

        // event listener
        filterCombo.addActionListener(e -> {
            String selected = (String) filterCombo.getSelectedItem();
            loadTransactions(selected);
        });

        refreshButton.addActionListener(e -> {
            String selected = (String) filterCombo.getSelectedItem();
            loadTransactions(selected);
        });

        // add to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    //create summary panel
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // These will be updated when transactions load
        JLabel countLabel = new JLabel("Total Transactions: 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 13));
        countLabel.setForeground(new Color(44, 62, 80));

        panel.add(countLabel);

        return panel;
    }

    // load transaction
    private void loadTransactions(String filter) {
        tableModel.setRowCount(0);

        try {
            List<Transaction> transactions;

            if ("ALL".equals(filter)) {
                transactions = transactionService.getAllTransactions();
            } else {
                transactions = transactionService.getTransactionsByType(filter);
            }

            if (transactions == null || transactions.isEmpty()) {
                return;
            }

            for (Transaction t : transactions) {
                // Format date
                String dateStr = t.getTimestamp().toString().replace("T", " ");
                if (dateStr.length() > 16) {
                    dateStr = dateStr.substring(0, 16);
                }

                // Handle null accounts
                String from = t.getFromAccountNumber();
                String to = t.getToAccountNumber();
                if (from == null) from = "-";
                if (to == null) to = "-";

                tableModel.addRow(new Object[]{
                        t.getTransactionId(),
                        dateStr,
                        t.getTransactionType(),
                        from,
                        to,
                        String.format("%.2f MAD", t.getAmount()),
                        t.getDescription()
                });
            }

            // Update summary (find the summary panel and update)
            updateSummary(transactions.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading transactions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // update summary
    private void updateSummary(int count) {
        // Find the summary panel at the bottom
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getLayout() instanceof FlowLayout) {
                    Component[] innerComps = panel.getComponents();
                    for (Component inner : innerComps) {
                        if (inner instanceof JLabel) {
                            ((JLabel) inner).setText("Total Transactions: " + count);
                            break;
                        }
                    }
                }
            }
        }
    }
}